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


def main():
    uvicorn.run(app, host="0.0.0.0", port=8000)


if __name__ == "__main__":
    main()
