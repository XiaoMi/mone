<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>报警邮件</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f9f9f9;
        }

        .container {
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
            background-color: #ffffff;
            border: 1px solid #dddddd;
            border-radius: 5px;
        }

        h3 {
            color: #ff0000;
        }

        p {
            margin: 10px 0;
        }

        strong {
            font-weight: bold;
        }

    </style>
</head>
<body>
<div class="container">
    <h3>报警信息</h3>
    <p><strong>应用：</strong>${application}</p>
    <p><strong>阈值：</strong>${alert_value}</p>
    <p><strong>IP：</strong>${ip}</p>
    <p><strong>serviceName：</strong>${serviceName}</p>
    <p><strong>methodName：</strong>${methodName}</p>
    <p><strong>开始时间：</strong>${start_time}</p>
</div>
</body>
</html>
