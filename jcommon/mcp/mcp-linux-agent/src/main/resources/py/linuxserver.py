from flask import Flask, request, jsonify
from linux import LinuxAutomation

app = Flask(__name__)
linux_automation = LinuxAutomation()


@app.route('/', methods=['GET'])
def index():
    return jsonify({"message": "Linux Server is running"})


@app.route('/capture_screen', methods=['GET'])
def capture_screen():
    grid = request.args.get('grid') == 'true'
    base64_str, mime_type, width, height, mouse_x, mouse_y = linux_automation.capture_fullscreen_jpg_base64(grid=grid)
    description = f"截图分辨率: {width}x{height}, 鼠标指针坐标: ({mouse_x}, {mouse_y}), 鼠标指针为红色x的交叉点。"
    return jsonify({"data": base64_str, "mime_type": mime_type, "description": description})


@app.route('/capture_grid', methods=['GET'])
def capture_grid():
    grid = request.args.get('grid')
    base64_str, mime_type = linux_automation.capture_jpg_base64_in_grid(grid_number=int(grid))
    return jsonify({"data": base64_str, "mime_type": mime_type, "description": ""})


@app.route('/move_mouse_to', methods=['POST'])
def move_mouse_to():
    data = request.get_json()
    x = data.get('x')
    y = data.get('y')
    if x is None or y is None:
        return jsonify({"error": "Missing parameters x or y"}), 400
    base64_str, mime_type, description, question = linux_automation.move_mouse_to(x, y)
    return jsonify({"data": base64_str, "mime_type": mime_type, "description": description, "question": question})


@app.route('/mouse_click', methods=['GET'])
def mouse_click():
    base64_str, mime_type, description, question = linux_automation.mouse_click()
    return jsonify({"data": base64_str, "mime_type": mime_type, "description": description, "question": question})


@app.route('/mouse_leftClick', methods=['GET'])
def mouse_leftClick():
    base64_str, mime_type, description, question = linux_automation.mouse_leftClick()
    return jsonify({"data": base64_str, "mime_type": mime_type, "description": description, "question": question})


@app.route('/mouse_doubleClick', methods=['GET'])
def mouse_doubleClick():
    base64_str, mime_type, description, question = linux_automation.mouse_doubleClick()
    return jsonify({"data": base64_str, "mime_type": mime_type, "description": description, "question": question})


@app.route('/keyboard_input_key', methods=['POST'])
def keyboard_input_key():
    data = request.get_json()
    key = data.get('key')
    if key is None:
        return jsonify({"error": "Missing parameter key"}), 400
    base64_str, mime_type, description, question = linux_automation.keyboard_input_key(key)
    return jsonify({"data": base64_str, "mime_type": mime_type, "description": description, "question": question})


@app.route('/keyboard_input_hotkey', methods=['POST'])
def keyboard_input_hotkey():
    data = request.get_json()
    keys = data.get('keys')
    if keys is None:
        return jsonify({"error": "Missing parameter keys"}), 400
    base64_str, mime_type, description, question = linux_automation.keyboard_input_hotkey(keys)
    return jsonify({"data": base64_str, "mime_type": mime_type, "description": description, "question": question})


@app.route('/keyboard_input_string', methods=['POST'])
def keyboard_input_string():
    data = request.get_json()
    text = data.get('text')
    if text is None:
        return jsonify({"error": "Missing parameter text"}), 400
    base64_str, mime_type, description, question = linux_automation.keyboard_input_string(text)
    return jsonify({"data": base64_str, "mime_type": mime_type, "description": description, "question": question})


@app.route('/execute_command', methods=['POST'])
def execute_command():
    data = request.get_json()
    command = data.get('command')
    if command is None:
        return jsonify({"error": "Missing parameter command"}), 400
    output = linux_automation.execute_command(command)
    return jsonify({"output": output})


@app.route('/execute_command_non_blocking', methods=['POST'])
def execute_command_non_blocking():
    data = request.get_json()
    command = data.get('command')
    if command is None:
        return jsonify({"error": "Missing parameter command"}), 400
    output = linux_automation.execute_command_non_blocking(command)
    return jsonify({"output": output})


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5001)
