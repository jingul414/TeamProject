/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const functions = require("firebase-functions");
const admin = require("firebase-admin");

// Firebase Admin SDK 초기화
admin.initializeApp();

// FCM 메시지 보내는 함수
// exports.sendPushNotification = functions.https.onRequest((req, res) => {
//   const token = req.body.token; // 클라이언트에서 받은 FCM 토큰
//   const title = req.body.title; // 메시지 제목
//   const body = req.body.body; // 메시지 내용

//   // 메시지 내용 설정
//   const message = {
//     token: token,
//     notification: {
//       title: title,
//       body: body,
//     },
//   };

//   // 메시지 전송
//   admin.messaging().send(message)
//       .then((response) => {
//         // 성공적인 응답
//         res.status(200).send(`Successfully sent message: ${response}`);
//       })
//       .catch((error) => {
//         // 에러 처리
//         res.status(500).send(`Error sending message: ${error}`);
//       });
// });

// 앱 내부 데이터 메시지 보내는 함수
exports.sendDataMessage = functions.https.onRequest((req, res) => {
  const token = req.body.token; // 대상 디바이스의 FCM 토큰
  const commandType = req.body.data.commandType;
  const commandValue = req.body.data.commandValue;

  // 데이터 메시지 구성
  const message = {
    data: {
      commandType: commandType,
      commandValue: commandValue,
    },
    token: token,
  };

  admin.messaging().send(message)
      .then((response) => {
        res.status(200).send("Data message sent successfully" + response);
      })
      .catch((error) => {
        res.status(500).send("Error sending data message: " + error);
      });
});

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
