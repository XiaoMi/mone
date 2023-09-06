<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Alarm email</title>
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
    <h3>Alarm information</h3>
    <p><strong>Application：</strong>${application}</p>
    <p><strong>Threshold：</strong>${alert_value}</p>
    <p><strong>IP：</strong>${ip}</p>
    <p><strong>serviceName：</strong>${serviceName}</p>
    <p><strong>methodName：</strong>${methodName}</p>
    <p><strong>Start time：</strong>${start_time}</p>
</div>
<p class="container" style="text-align:center;">
    <a href="http://${silence_url}" style="background-color:#4CAF50; color:white; padding:10px 20px; border:none; border-radius:5px; text-decoration:none;">Silence the alarm</a>
</p>
</body>
</html>
