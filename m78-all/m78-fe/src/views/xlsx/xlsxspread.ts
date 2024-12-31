/*! xlsxspread.js (C) SheetJS LLC -- https://sheetjs.com/ */
/* eslint-env browser */
/*global XLSX */
/*exported stox, xtos */

/**
 * Converts data from SheetJS to x-spreadsheet
 *
 * @param  {Object} wb SheetJS workbook object
 *
 * @returns {Object[]} An x-spreadsheet data
 */
import * as XLSX from 'xlsx'

export function stox(wb) {
  const out = []
  console.log('wb', wb)
  wb.SheetNames.forEach(function (name) {
    const o = { name: name, rows: {} }
    const ws = wb.Sheets[name]
    console.log('ws', ws)
    if (!ws || !ws['!ref']) return
    const range = XLSX.utils.decode_range(ws['!ref'])
    // sheet_to_json will lost empty row and col at begin as default
    range.s = { r: 0, c: 0 }
    const aoa = XLSX.utils.sheet_to_json(ws, {
      raw: false,
      header: 1,
      range: range
    })
    console.log('aoa', aoa)
    aoa.forEach(function (r, i) {
      const cells = {}
      r.forEach(function (c, j) {
        cells[j] = { text: c || String(c) }

        const cellRef = XLSX.utils.encode_cell({ r: i, c: j })

        if (ws[cellRef] != null && ws[cellRef].f != null) {
          cells[j].text = '=' + ws[cellRef].f
        }
      })
      o.rows[i] = { cells: cells }
    })
    o.rows.len = aoa.length

    o.merges = []
    ;(ws['!merges'] || []).forEach(function (merge, i) {
      //Needed to support merged cells with empty content
      if (o.rows[merge.s.r] == null) {
        o.rows[merge.s.r] = { cells: {} }
      }
      if (o.rows[merge.s.r].cells[merge.s.c] == null) {
        o.rows[merge.s.r].cells[merge.s.c] = {}
      }

      o.rows[merge.s.r].cells[merge.s.c].merge = [merge.e.r - merge.s.r, merge.e.c - merge.s.c]

      o.merges[i] = XLSX.utils.encode_range(merge)
    })

    out.push(o)
  })

  return out
}

/**
 * Converts data from x-spreadsheet to SheetJS
 *
 * @param  {Object[]} sdata An x-spreadsheet data object
 *
 * @returns {Object} A SheetJS workbook object
 */
export function xtos(sdata) {
  const out = XLSX.utils.book_new()
  sdata.forEach(function (xws) {
    const ws = {}
    const rowobj = xws.rows
    const minCoord = { r: 0, c: 0 },
      maxCoord = { r: 0, c: 0 }
    for (var ri = 0; ri < rowobj.len; ++ri) {
      var row = rowobj[ri]
      if (!row) continue

      Object.keys(row.cells).forEach(function (k) {
        const idx = +k
        if (isNaN(idx)) return

        const lastRef = XLSX.utils.encode_cell({ r: ri, c: idx })
        if (ri > maxCoord.r) maxCoord.r = ri
        if (idx > maxCoord.c) maxCoord.c = idx

        let cellText = row.cells[k].text,
          type = 's'
        if (!cellText) {
          cellText = ''
          type = 'z'
        } else if (!isNaN(Number(cellText))) {
          cellText = Number(cellText)
          type = 'n'
        } else if (cellText.toLowerCase() === 'true' || cellText.toLowerCase() === 'false') {
          cellText = Boolean(cellText)
          type = 'b'
        }

        ws[lastRef] = { v: cellText, t: type }

        if (type == 's' && cellText[0] == '=') {
          ws[lastRef].f = cellText.slice(1)
        }

        if (row.cells[k].merge != null) {
          if (ws['!merges'] == null) ws['!merges'] = []

          ws['!merges'].push({
            s: { r: ri, c: idx },
            e: {
              r: ri + row.cells[k].merge[0],
              c: idx + row.cells[k].merge[1]
            }
          })
        }
      })
    }
    ws['!ref'] = minCoord
      ? XLSX.utils.encode_range({
          s: minCoord,
          e: maxCoord
        })
      : 'A1'

    XLSX.utils.book_append_sheet(out, ws, xws.name)
  })

  return out
}
