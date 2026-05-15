import * as admin from "firebase-admin";
import { onValueUpdated } from "firebase-functions/v2/database";

admin.initializeApp();

export const notifyUserAvailable = onValueUpdated(
  "/users/{userId}/available",
  async (event) => {

    const isAvailable = event.data.after.val();
    const userId = event.params.userId;

    if (isAvailable === true) {

      const userSnapshot = await admin.database()
        .ref(`/users/${userId}`)
        .once("value");

      const userData = userSnapshot.val();

      return admin.messaging().send({
        topic: "available_users",
        notification: {
          title: "¡Nuevo usuario disponible!",
          body: `${userData.name} ${userData.lastname} se ha conectado.`,
        },
        data: {
          targetUserId: userId,
        },
      });
    }

    return null;
  }
);