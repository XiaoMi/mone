import MarkdownIt from "markdown-it";
import * as htmlparser2 from "htmlparser2";

type StockData = Record<string, string>;

interface StockOrder {
  symbol: string;
  market: string;
  secType: string;
  currency: string;
  expiry: string;
  strike: string;
  right: string;
  action: string;
  orderType: string;
  status: string;
  filledQuantity: number;
  avgFillPrice: number;
  commission: number;
}

interface StockOrderResponse {
  item: {
    orders: StockOrder[];
  };
  code: number;
  message: string;
}

export function markdownItStock(md: MarkdownIt) {
  if (!md.block || !md.block.ruler) {
    console.warn("markdown-it-stock: block ruler not found");
    return;
  }

  function parseStockBlock(
    state: any,
    startLine: number,
    endLine: number,
    silent: boolean
  ): boolean {
    const pos = state.bMarks[startLine] + state.tShift[startLine];

    // 检查是否包含相关标签
    const text = state.src.slice(pos);
    if (!text.includes("<stock-transaction>") && !text.includes("<stock-order>")) {
      return false;
    }

    if (silent) return true;

    let html = "";
    let jsonContentMap = new Map<string, string[]>();
    let tagname = "";
    let isCollectingJson = false;

    const parser = new htmlparser2.Parser({
      onopentag(name) {
        if (name === "stock-transaction" || name === "stock-order") {
          console.log('onopentag', name);
          isCollectingJson = true;
          tagname = name;
          jsonContentMap.get(tagname) == null && jsonContentMap.set(tagname, [])
        }
      },
      ontext(text) {
        console.log('ontext', tagname, text);
        if (tagname && isCollectingJson) {
          jsonContentMap.get(tagname)?.push(text)
        }
      },
      onclosetag(name, isImplied) {
        if (name === "stock-transaction" || name === "stock-order") {
          console.log('onclosetag', 'jsonContentMap', jsonContentMap.get(name));
          const jsonContent = jsonContentMap.get(name)?.pop();
          isCollectingJson = false;
          tagname = "";
          console.log('onclosetag', name, jsonContent);
          try {
            if (jsonContent == null) {
              return;
            }
            if (name === "stock-transaction") {
              const data = JSON.parse(jsonContent.trim()) as StockData;
              html = generateTransactionHtml(data);
            } else {
              const data = JSON.parse(jsonContent.trim()) as StockOrderResponse;
              html = generateOrderHtml(data);
            }
          } catch (e) {
            html = generateErrorHtml();
          }
        }
      }
    }, {
      xmlMode: true,
    });

    parser.write(text);
    parser.end();

    let token = state.push("html_block", "", 0);
    token.content = html;
    token.map = [startLine, endLine + 1];

    state.line = endLine + 1;

    return true;
  }

  // 生成普通内容HTML
  function generateContentHtml(content: string): string {
    return `
    <div class="stock-account-block">
      <div class="account-header">
        <i class="fa-solid fa-money-bill-wave"></i>
        <span>账户资产</span>
      </div>
      <div class="account-content">
        <div class="content-message">${content}</div>
      </div>
    </div>`;
  }

  // 生成交易账户HTML内容
  function generateTransactionHtml(data: StockData): string {
    return `
    <div class="stock-account-block">
      <div class="account-header">
        <i class="fa-solid fa-money-bill-wave"></i>
        <span>账户资产 (${data.Currency || 'USD'})</span>
      </div>
      <div class="account-content">
        <div class="summary-content">
          ${Object.entries(data)
            .filter(([key]) => key !== 'Currency')
            .map(([key, value]) => `
            <div class="summary-item">
              <span class="label">${formatLabel(key)}：</span>
              <span class="value ${value === 'N/A' ? 'na' : ''}">${formatValue(value)}</span>
            </div>
          `).join('')}
        </div>
      </div>
    </div>`;
  }

  // 生成订单HTML内容
  function generateOrderHtml(data: StockOrderResponse): string {
    if (!data.item?.orders?.length) {
      return generateContentHtml('暂无订单数据');
    }

    return `
    <div class="stock-order-block">
      <div class="order-header">
        <i class="fa-solid fa-list"></i>
        <span>期权订单列表</span>
      </div>
      <div class="order-content">
        <div class="order-table">
          <table>
            <thead>
              <tr>
                <th>代码</th>
                <th>类型</th>
                <th>方向</th>
                <th>数量</th>
                <th>成交价</th>
                <th>状态</th>
                <th>手续费</th>
              </tr>
            </thead>
            <tbody>
              ${data.item.orders.map(order => `
                <tr>
                  <td>${order.symbol} ${order.expiry} ${order.strike}${order.right}</td>
                  <td>${order.orderType}</td>
                  <td>${formatAction(order.action)}</td>
                  <td>${order.filledQuantity}</td>
                  <td>${formatPrice(order.avgFillPrice)}</td>
                  <td>${formatStatus(order.status)}</td>
                  <td>${formatPrice(order.commission)}</td>
                </tr>
              `).join('')}
            </tbody>
          </table>
        </div>
      </div>
    </div>`;
  }

  // 生成错误HTML
  function generateErrorHtml(): string {
    return `
    <div class="stock-account-block error">
      <div class="account-header">
        <i class="fa-solid fa-exclamation-triangle"></i>
        <span>数据解析错误</span>
      </div>
      <div class="account-content">
        <div class="error-message">无法解析数据</div>
      </div>
    </div>`;
  }

  // 格式化标签名称
  function formatLabel(key: string): string {
    const labelMap: Record<string, string> = {
      '现金余额': '现金余额',
      '可用交易现金': '可用资金',
      '总持仓价值': '持仓市值',
      'Currency': '币种'
    };
    return labelMap[key] || key;
  }

  // 格式化数值
  function formatValue(value: string): string {
    if (value === 'N/A') {
      return '暂无数据';
    }
    // 如果是数字字符串，添加千位分隔符
    if (!isNaN(Number(value))) {
      return Number(value).toLocaleString('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      });
    }
    return value;
  }

  // 格式化价格
  function formatPrice(value: number): string {
    return value.toLocaleString('en-US', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
  }

  // 格式化交易方向
  function formatAction(action: string): string {
    return action === 'BUY' ? '买入' : '卖出';
  }

  // 格式化订单状态
  function formatStatus(status: string): string {
    const statusMap: Record<string, string> = {
      'Filled': '已成交',
      'Cancelled': '已取消',
      'Invalid': '无效',
      'Pending': '待处理'
    };
    return statusMap[status] || status;
  }

  md.block.ruler.before("html_block", "stock", parseStockBlock, {
    alt: ["paragraph", "reference", "blockquote"],
  });
}
