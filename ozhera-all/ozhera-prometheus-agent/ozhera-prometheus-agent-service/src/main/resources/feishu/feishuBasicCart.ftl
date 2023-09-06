{
"config": {
"wide_screen_mode": true
},
"elements": [
{
"tag": "div",
"text": {
"content": "**Application**: ${application}\n**Threshold**: ${alert_value}\n**ip**: ${ip}\n**pod**: ${pod}\n**Start Time**: ${start_time}",
"tag": "lark_md"
}
},
{
"actions": [
{
"tag": "button",
"text": {
"content": "Silence the alarm",
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