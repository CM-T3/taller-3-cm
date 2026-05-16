import * as admin from "firebase-admin";
import { onValueUpdated } from "firebase-functions/v2/database";

admin.initializeApp();

export const notifyUserAvailable = onValueUpdated(
  "/users/{userId}/available",
  async (event) => {
    const isAvailable = event.data.after.val();
    const userId = event.params.userId;

    if (isAvailable !== true) return null;

    const userSnapshot = await admin.database()
      .ref(`/users/${userId}`)
      .once("value");
    const userData = userSnapshot.val();
    if (!userData) return null;

    const allUsersSnapshot = await admin.database()
      .ref("/users")
      .once("value");

    const tokens: string[] = [];
    allUsersSnapshot.forEach((child) => {
      const uid = child.key;
      const user = child.val();

      if (uid !== userId && user.fcmToken) {
        tokens.push(user.fcmToken);
      }
    });

    if (tokens.length === 0) return null;

    const message: admin.messaging.MulticastMessage = {
      tokens: tokens,
      data: {
        targetUserId: userId,
        title: "¡Nuevo usuario disponible!",
        body: `${userData.name} ${userData.lastname} se ha conectado.`,
      },
      android: {
        priority: "high",
        ttl: 0,          )
      },
    };

    const response = await admin.messaging().sendEachForMulticast(message);
    console.log(
      `Enviadas: ${response.successCount} exitosas, ${response.failureCount} fallidas de ${tokens.length} tokens`
    );
    return null;
  }
);