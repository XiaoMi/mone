{
"config": {
"wide_screen_mode": true
},
"header": {
"template": "orange",
"title": {
"tag": "plain_text",
"content": "[${priority}][Hera]  ${title} ${alert_op} ${alert_value}"
}
},
"elements": [
{
"tag": "div",
"text": {
"content": "**应用**: ${application}\n**阈值**: ${alert_value}\n**ip**: ${ip}\n**serviceName**: ${serviceName}\n**methodName**: ${methodName}\n**开始时间**: ${start_time}",
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
"url": "${silence_url}",
"pc_url": "",
"android_url": "",
"ios_url": ""
}
}
],
"tag": "action"
}
]
}