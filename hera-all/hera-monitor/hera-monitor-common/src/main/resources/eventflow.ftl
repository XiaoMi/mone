{
"size": "1111111111${size}",
"query": {
"constant_score": {
"filter": {
"range": {
"last_updated": {
"gte": ${gte_val},
"lte": ${lte_val}
}
}
}
}
},
"aggs": {
"by_time": {
"date_histogram": {
"field": "last_updated",
"interval": "${interval}",
"extended_bounds": {
"min": "${min_val}",
"max": "${max_val}"
}
},
"aggs": {
"event_status_group": {
"filters": {
"filters": {
"info": {
"match_phrase": {
"alert_status": "info"
}
},
"warning": {
"match_phrase": {
"alert_status": "warning"
}
},
"error": {
"match_phrase": {
"alert_status": "error"
}
},
"success": {
"match_phrase": {
"alert_status": "success"
}
}
}
}
}
}
}
}
}