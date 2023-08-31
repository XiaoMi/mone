{
"config": {
"wide_screen_mode": true
},
"elements": [
{
"tag": "div",
"text": {
"content": "${content}",
"tag": "lark_md"
}
},
{
"actions": [
{
"tag": "button",
"text": {
"content": "报警静默",
"tag": "plain_text"
},
"type": "primary",
"multi_url": {
"url": "http://${silence_url}",
"pc_url": "",
"android_url": "",
"ios_url": ""
}
}
],
"tag": "action"
}
],
"header": {
"template": "orange",
"title": {
"content": "[${priority}][Hera]  ${title} ${alert_op} ${alert_value}",
"tag": "plain_text"
}
}
}