const {onRequest} = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");
const {setGlobalOptions} = require("firebase-functions/v2");

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// set service account has not affect in firebase functions, only available in cloud functions
setGlobalOptions({
  serviceAccount: "webhooks@com-sommerengineering-baraudio" +
  ".iam.gserviceaccount.com",
});

exports.baraudio = onRequest(
    {
      region: "us-east1",
    },
    (request, response) => {
      logger.info("Webhooks log entry", {structuredData: true});
      response.send("\nWebhooks response ...\n");
    });
