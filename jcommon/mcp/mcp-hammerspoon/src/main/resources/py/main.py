from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import window_util
import input_util
import uvicorn
from typing import Optional
import input
app = FastAPI()


class Message(BaseModel):
    contactName: str
    message: str


class Contact(BaseModel):
    contactName: str

class Coordinate(BaseModel):
    x: float
    y: float

class MergeImagesRequest(BaseModel):
    image1: str
    image2: str
    mimeType1: str
    mimeType2: str

@app.get("/capture_window")
async def capture_window():
    try:
        img_base64, mimetype = window_util.capture_window("企业微信")
        return {"image": img_base64, "mimetype": mimetype}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/searchAndSendWeComMessage")
async def search_and_send_wecom_message(msg: Message):
    try:
        input_util.searchAndSendWeComMessage(msg.contactName, msg.message)
        return {"message": "Message sent successfully"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/searchWeComContact")
async def search_wecom_contact(contact: Contact):
    try:
        input_util.searchWeComContact(contact.contactName)
        return {"message": "Contact searched successfully"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/next_unread_message")
async def next_unread_message():
    try:
        print('lalalallalala')
        input.switchToNextUnreadMessage()
        return {"message": "Switched to next unread message successfully"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/viewChatContentUpward")
async def viewChatContentUpward():
    try:
        input_util.viewChatContentUpward()
        return {"message": "Switched to next unread message successfully"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/viewChatContentBelow")
async def viewChatContentBelow():
    try:
        input_util.viewChatContentBelow()
        return {"message": "Switched to next unread message successfully"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/merge_images")
async def merge_images(request: MergeImagesRequest):
    try:
        result = input_util.merge_images(
            request.image1,
            request.image2,
            request.mimeType1,
            request.mimeType2
        )
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/is_window_maximized")
async def check_window_maximized():
    """
    检查企业微信窗口是否最大化
    """
    try:
        hwnd = input_util.findWorkWechatAndTopMost()
        if not hwnd:
            raise HTTPException(status_code=404, detail="未找到企业微信窗口")
        is_max = input_util.is_window_maximized(hwnd)
        return {"maximized": is_max}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/maximize_window")
async def maximize_window():
    """
    最大化企业微信窗口
    """
    try:
        hwnd = input_util.findWorkWechatAndTopMost()
        if not hwnd:
            raise HTTPException(status_code=404, detail="未找到企业微信窗口")
        input_util.maximize_window(hwnd)
        return {"message": "窗口已最大化"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/click_input_box")
async def click_input_box():
    """
    点击企业微信聊天窗口的输入框
    """
    try:
        hwnd = input_util.findWorkWechatAndTopMost()
        if not hwnd:
            raise HTTPException(status_code=404, detail="未找到企业微信窗口")
        
        # 确保窗口最大化
        if not input_util.is_window_maximized(hwnd):
            input_util.maximize_window(hwnd)
        
        # 点击输入框
        success = input_util.click_input_box()
        if not success:
            raise HTTPException(status_code=500, detail="点击输入框失败")
        
        return {"message": "已点击输入框"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/click_chat_area")
async def click_chat_area_endpoint():
    """
    点击企业微信聊天窗口的聊天区域
    """
    try:
        success = input_util.click_chat_area()
        if not success:
            raise HTTPException(status_code=500, detail="点击聊天区域失败")
        return {"message": "已点击聊天区域"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

def main():
    uvicorn.run(app, host="0.0.0.0", port=8000)


if __name__ == "__main__":
    main()
