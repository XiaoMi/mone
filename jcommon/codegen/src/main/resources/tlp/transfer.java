package run.mone.model.transfer;

import run.mone.api.vo.${className}VO;
import run.mone.model.po.${className};
import com.xiaomi.youpin.docean.common.BeanUtils;


/**
 * @author ${author}
 */
public class ${className}Transfer {

    public static ${className}VO po2vo(${className} po) {
        ${className}VO vo = new ${className}VO();
        BeanUtils.copy(po, vo);
        return vo;
    }

    public static ${className} vo2po(${className}VO vo) {
        ${className} po = new ${className}();
        BeanUtils.copy(vo, po);
        return po;
    }
}