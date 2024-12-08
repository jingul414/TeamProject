/**
 * 각 서브 모듈에서 함수 트리거 가져오기:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * 지원되는 트리거 목록은 https://firebase.google.com/docs/functions에서 확인하세요.
 */
"use strict";
// Firebase SDK의 Cloud Functions를 사용하여 Cloud Functions와 트리거를 생성합니다.
const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

// 첫 번째 함수 생성 및 배포
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", { structuredData: true });
//   response.send("Firebase에서 인사드립니다!");
// });

// // 이 HTTP 엔드포인트로 전달된 text 파라미터를 가져와
// // Firestore의 /messages/:documentId/original 경로에 삽입합니다.
// exports.addmessage = onRequest(async (req, res) => {
//   // text 파라미터를 가져옵니다.
//   const original = req.query.text;
//   // Firebase Admin SDK를 사용하여 Firestore에 새로운 메시지를 추가합니다.
//   const writeResult = await getFirestore()
//     .collection("messages")
//     .add({ original: original });
//   // 메시지를 성공적으로 작성했다는 응답을 전송합니다.
//   res.json({ result: `ID가 ${writeResult.id}인 메시지가 추가되었습니다.` });
// });

// exports.sendCommand = functions.https.onRequest((req, res) => {
//   // 요청 본문에서 FCM 메시지 정보 추출
//   const token = req.body.token; // 기기의 토큰
//   const type = req.body.data.type;
//   const payload = req.body.data.payload;

//   if (!token || !type || !payload) {
//     return res.status(400).send("Missing fields");
//   }

//   // 메시지 작성
//   const message = {
//     token: token,
//     data: {
//       type: type,
//       payload: {
//         ...payload,
//       },
//     },
//   };

//   admin.messaging().send(message).then((response) => {
//     console.log("Successfully sent message:", response);
//     res.status(200).send(`Successfully sent message: ${response}`);
//   }).catch((error) => {
//     console.error("Error sending message:", error);
//     res.status(500).send(`Error sending message. =>> ${error}`);
//   });
// });
exports.sendCommand = functions.https.onRequest((req, res) => {
  try {
    // 요청 본문에서 FCM 메시지 정보 추출
    const token = req.body.token;
    const priority = req.body.priority;
    const data = req.body.data;

    // 필드 유효성 검사
    if (!token) {
      return res.status(400).send("Missing fields: token");
    }
    if (!priority) {
      return res.status(400).send("Missing fields: priority");
    }
    if (!data) {
      return res.status(400).send("Missing fields: data");
    }
    if (!data.type) {
      return res.status(400).send("Missing fields: data.type");
    }
    if (!data.payload) {
      return res.status(400).send("Missing fields: data.payload");
    }
    // payload가 객체라면 문자열로 변환
    let payloadString;
    if (typeof data.payload === "object") {
      payloadString = JSON.stringify(data.payload);
    } else if (typeof data.payload === "string") {
      payloadString = data.payload;
    } else {
      return res.status(400).send("Invalid type: data.payload");
    }
    // 메시지 작성
    const message = {
      token: token,
      android: {
        priority: priority, // Android용 priority 설정
      },
      data: {
        type: data.type,
        payload: payloadString, // 이미 JSON 문자열로 변환된 상태로 그대로 사용
      },
    };

    // FCM 메시지 전송
    admin.messaging()
        .send(message)
        .then((response) => {
          console.log("Successfully sent message:", response);
          res.status(200).send(`Successfully sent message: ${response}`);
        })
        .catch((error) => {
          console.error("Error sending message:", error);
          res.status(500).send("Failed to send message");
        });
  } catch (error) {
    console.error("Unexpected error:", error);
    res.status(500).send("Unexpected server error");
  }
});
