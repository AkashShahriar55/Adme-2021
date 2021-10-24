/* eslint-disable linebreak-style */
/* eslint-disable require-jsdoc */
/** quotation */
/** welcome */
/** profile */
/** none */
const functions = require("firebase-functions");

const admin = require("firebase-admin");
admin.initializeApp();
const db = admin.firestore();
const GeoPoint = require("geopoint");

const Fuse = require("fuse.js");
const monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun",
  "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
];

const duePercentage = 0.1;

/** Create entry in Adme_Service List  when service created for the first time*/
exports.createService = functions.firestore
    .document("Adme_User/{userId}/data/service_provider/services/{serviceId}")
    .onCreate((snap, context) => {
      // Get an object representing the document
      // e.g. {'name': 'Marie', 'age': 66}
      const service = snap.data();

      // access a particular field as you would any JS property

      const category = service.category;
      const categoryId = service.categoryId;
      const description = service.description;
      const latitude = service.latitude;
      const longitude = service.longitude;
      const serviceId = context.params.serviceId;
      const UserId = context.params.userId;
      const picUrl = service.pic_url;
      const rating = service.rating;
      const reviews = service.reviews;
      const tags = service.tags;
      const userName = service.user_name;

      const p = db.collection("Adme_Service_list").doc(serviceId).set({
        category: category,
        categoryId: categoryId,
        description: description,
        latitude: latitude,
        longitude: longitude,
        pic_url: picUrl,
        rating: rating,
        reviews: reviews,
        tags: tags,
        user_name: userName,
        min_charge: 0,
        max_charge: 0,
        user_ref: UserId,
      });

      return p;
    });

/** Update entry in  Adme_Service List when service updated*/
/** User name and profile image */
exports.updateService = functions.firestore
    .document("Adme_User/{userId}/data/service_provider/services/{serviceId}")
    .onUpdate((snap, context) => {
      // Get an object representing the document
      // e.g. {'name': 'Marie', 'age': 66}
      // const service = snap.data();
      const newValue = snap.after.data();

      // ...or the previous value before this update
      // const previousValue = snap.before.data();

      // access a particular field as you would any JS property

      const category = newValue.category;
      const categoryId = newValue.categoryId;
      const description = newValue.description;
      const latitude = newValue.latitude;
      const longitude = newValue.longitude;
      const serviceId = context.params.serviceId;
      const UserId = context.params.userId;
      const picUrl = newValue.pic_url;
      const rating = newValue.rating;
      const reviews = newValue.reviews;
      const tags = newValue.tags;
      const userName = newValue.user_name;

      const p = db.collection("Adme_Service_list").doc(serviceId).set({
        category: category,
        categoryId: categoryId,
        description: description,
        latitude: latitude,
        longitude: longitude,
        pic_url: picUrl,
        rating: rating,
        reviews: reviews,
        tags: tags,
        user_name: userName,
        min_charge: 0,
        max_charge: 0,
        user_ref: UserId,
      });

      return p;
    });


/** Searching Service*/
exports.getServiceSearchResults = functions.https.onCall((data, context) => {
  const serviceRef = db.collection("Adme_Service_list");
  const docs = [];
  const finalJson = [];
  // const fial_jsn = [];
  const options = {
    includeScore: false,
    keys: ["tags"],
    shouldSort: true,
  };
  console.log(data);
  return serviceRef.get().then((snapshot) => {
    if (snapshot.empty) {
      console.log("No matching documents.");
      throw new functions.https.HttpsError("Error", "Found Empty Data");
    } else {
      console.log("Found matching documents.");
      snapshot.forEach((doc) => {
        const obj = {
          "id": doc.id,
          "category": doc.data().category,
          "categoryId": doc.data().categoryId,
          "description": doc.data().description,
          "latitude": doc.data().latitude,
          "longitude": doc.data().longitude,
          "max_charge": doc.data().max_charge,
          "min_charge": doc.data().min_charge,
          "pic_url": doc.data().pic_url,
          "rating": doc.data().rating,
          "reviews": doc.data().reviews,
          "user_name": doc.data().user_name,
          "tags": doc.data().tags,
          "user_ref": doc.data().user_ref,
        };
        // var data = doc.id
        docs.push(obj);
      });
      const fuse = new Fuse(docs, options);
      const result = fuse.search(data.toLocaleLowerCase());
      const jsn = JSON.parse(JSON.stringify(result));
      console.log("Json : ");
      console.log(jsn);
      jsn.forEach((index) => {
        const res = index.item;
        delete res["tags"];
        finalJson.push(res);
      });


      return {
        data: JSON.stringify(finalJson),
      };
    }
  });
});

/** Notification */
/** Send Welcome Notification after user creation */
exports.welcomeNotification = functions.firestore
    .document("Adme_User/{userId}")
    .onCreate((snap, context) => {
      // Get an object representing the document
      // e.g. {'name': 'Marie', 'age': 66}
      const user = snap.data();

      // access a particular field as you would any JS property
      const promises = [];


      const UserId = context.params.userId;

      const notifText = "Welcome " + user.user_name +
                        ". Thanks for signing up with Adme.";
      const notifTime = admin.firestore.FieldValue.serverTimestamp();
      const logo = "https://firebasestorage.googleapis.com/v0/b/adme-bf48a.appspot.com" +
                    "/o/app_icons%2Flogo.png?alt="+
                    "media&token=d4a9ee46-b413-4606-8fd8-00ee6b737dfd";

      const p = db.collection("Adme_User/"+UserId+"/notification_list").add({
        text: notifText,
        time: notifTime,
        mode: "both",
        type: "welcome",
        reference: null,
        img_url: logo,
        isSeen: true,
        pushable: false,

      });
      promises.push(p);

      const p2 = db.collection("Adme_User").doc(UserId)
          .set({
            hasUnreadNotifSP: true,

          }, {merge: true});
      promises.push(p2);

      const p3 = db.collection("Adme_User").doc(UserId)
          .set({
            hasUnreadNotifClient: true,

          }, {merge: true});
      promises.push(p3);

      return Promise.all(promises);
    });

/** Update on user data updated */
exports.updateOwnService = functions.firestore
    .document("Adme_User/{userId}")
    .onUpdate((snap, context) => {
      // Get an object representing the document
      // e.g. {'name': 'Marie', 'age': 66}
      // const user = snap.data();
      const newValue = snap.after.data();
      // ...or the previous value before this update
      const previousValue = snap.before.data();
      const oldserName = previousValue.user_name;
      const newUserName = newValue.user_name;
      const oldUserPic = previousValue.profile_image_url;
      const newUserPic = newValue.profile_image_url;
      const promises = [];

      // access a particular field as you would any JS property
      const UserId = context.params.userId;
      if (oldserName !== newUserName) {
        // update username inside own service

        db.collection("Adme_User/"+UserId+"/data" +
        "/service_provider/services")
            .get().then((snapshot) => {
              snapshot.forEach((doc) => {
                const p = doc.ref
                    .set({
                      user_name: newUserName,
                      // pic_url,

                    }, {merge: true});
                promises.push(p);
              });
            })
            .catch((err) => {
              console.log("Error getting documents updateOwnService"+
              " username", err);
            });
        /** end */
        /** update chat user user_name */
        const p1 = db.collection("users")
            .doc(UserId)
            .set({
              username: newUserName,
              // pic_url,

            }, {merge: true});
        promises.push(p1);
      }
      if (oldUserPic !== newUserPic) {
        // update profile pic inside own Service
        db.collection("Adme_User/"+UserId+"/data" +
        "/service_provider/services")
            .get().then((snapshot) => {
              snapshot.forEach((doc) => {
                const p = doc.ref
                    .set({
                      pic_url: newUserPic,
                      // pic_url,

                    }, {merge: true});
                promises.push(p);
              });
            })
            .catch((err) => {
              console.log("Error getting documents updateOwnService"+
              " username", err);
            });
        /** end */
        /** update chat user user_name */
        const p1 = db.collection("users")
            .doc(UserId)
            .set({
              photoUrl: newUserPic,
              // pic_url,

            }, {merge: true});
        promises.push(p1);
      }


      return Promise.all(promises);
    });


/** Notification */
//* *Send Notification on creation of appoinment List */
exports.quotationNotification = functions.firestore
    .document("Adme_Appointment_list/{appointmentId}")
    .onCreate((snap, context) => {
      // Get an object representing the document
      // e.g. {'name': 'Marie', 'age': 66}
      const appointment = snap.data();

      // access a particular field as you would any JS property


      const appointmentId = context.params.appointmentId;

      const notifTextSp = appointment.client_name +
                        " has sent you a Quotation";
      const notifTextClient = "Your Quotation has been submitted " +
                          "to " + appointment.service_provider_name;
      const notifTime = admin.firestore.FieldValue.serverTimestamp();

      const spId = appointment.service_provider_ref;
      const clinetId = appointment.client_ref;
      const clientPic = appointment.client_profile_pic;
      const spPic = appointment.service_provider_pic;

      const promises = [];

      const p1 = db.collection("Adme_User/"+spId+"/notification_list").add({
        text: notifTextSp,
        time: notifTime,
        mode: "service_provider",
        type: "quotation",
        reference: appointmentId,
        img_url: clientPic,
        isSeen: false,
        pushable: true,

      });

      promises.push(p1);

      const p2 = db.collection("Adme_User/"+clinetId+"/notification_list").add({
        text: notifTextClient,
        time: notifTime,
        mode: "client",
        type: "quotation",
        reference: appointmentId,
        img_url: spPic,
        isSeen: false,
        pushable: false,

      });

      promises.push(p2);

      const p3 = db.collection("Adme_User/"+spId+"/data")
          .doc("service_provider")
          .update({
            requested: admin.firestore.FieldValue.increment(1),
          });

      promises.push(p3);

      const p4 = db.collection("Adme_User").doc(spId)
          .set({
            hasUnreadNotifSP: true,

          }, {merge: true});
      promises.push(p4);

      const p5 = db.collection("Adme_User").doc(clinetId)
          .set({
            hasUnreadNotifClient: true,

          }, {merge: true});
      promises.push(p5);

      return Promise.all(promises);
    });

// delete all notification when a appointment is deleted
exports.deleteAppointment= functions.firestore
    .document("Adme_Appointment_list/{appointmentId}")
    .onDelete((snap, context) => {
      const deletedValue = snap.data();
      const clientId = deletedValue.client_ref;
      const spId = deletedValue.service_provider_ref;
      const appointmentId = context.params.appointmentId;
      const promises = [];

      db.collection("Adme_User/"+spId+"/notification_list")
          .where("reference", "==", appointmentId)
          .get().then((snapshot) => {
            snapshot.forEach((doc) => {
              const p = doc.ref.delete();
              promises.push(p);
            });
          })
          .catch((err) => {
            console.log("Error getting documents", err);
          });

      db.collection("Adme_User/"+clientId+"/notification_list")
          .where("reference", "==", appointmentId)
          .get().then((snapshot) => {
            snapshot.forEach((doc) => {
              const p = doc.ref.delete();
              promises.push(p);
            });
          })
          .catch((err) => {
            console.log("Error getting documents", err);
          });

      // const p4 = db.collection("Adme_User").doc(spId)
      //     .set({
      //       hasUnreadNotifSP: false,

      //     }, {merge: true});
      // promises.push(p4);

      // const p5 = db.collection("Adme_User").doc(clientId)
      //     .set({
      //       hasUnreadNotifClient: false,

      //     }, {merge: true});
      // promises.push(p5);
      return Promise.all(promises);
    });


/** Notification */
// send notification on response of quotation
exports.updateQuotationNotification = functions.firestore
    .document("Adme_Appointment_list/{appointmentId}")
    .onUpdate((change, context) => {
      // Get an object representing the document
      // e.g. {'name': 'Marie', 'age': 66}
      const newValue = change.after.data();

      // ...or the previous value before this update
      // const previousValue = change.before.data();

      const status = newValue.state;

      const appointmentId = context.params.appointmentId;

      /** const notifTextSp = appointment.client_name +
                        " has sent you a Quotation";
      const notifTextClient = "Your Quotation has been submitted " +
                          "to " + appointment.service_provider_name;
       **/

      const spId = newValue.service_provider_ref;
      const clinetId = newValue.client_ref;
      const clientPic = newValue.client_profile_pic;
      const spPic = newValue.service_provider_pic;
      const totalIncome = newValue.total_income;

      if (status === "provider_response_sent") {
        const notifTime = admin.firestore.FieldValue.serverTimestamp();
        const notifText = newValue.service_provider_name +
                          " has sent a response to" +
                          " your Quotation";
        const promises = [];

        const p = db.collection("Adme_User/"+clinetId+"/notification_list")
            .add({
              text: notifText,
              time: notifTime,
              mode: "client",
              type: "quotation",
              reference: appointmentId,
              img_url: spPic,
              isSeen: false,
              pushable: true,

            });
        promises.push(p);

        const p2 = db.collection("Adme_User").doc(clinetId)
            .set({
              hasUnreadNotifClient: true,

            }, {merge: true});
        promises.push(p2);


        return Promise.all(promises);
      } else if (status === "provider_response_approve") {
        const notifTime = admin.firestore.FieldValue.serverTimestamp();
        const notifText = newValue.client_name +
                          " approved" +
                          " your work resoponse";
        const promises = [];

        const p = db.collection("Adme_User/"+spId+"/notification_list")
            .add({
              text: notifText,
              time: notifTime,
              mode: "service_provider",
              type: "quotation",
              reference: appointmentId,
              img_url: clientPic,
              isSeen: false,
              pushable: true,

            });

        promises.push(p);

        const p2 = db.collection("Adme_User").doc(spId)
            .set({
              hasUnreadNotifSP: true,

            }, {merge: true});
        promises.push(p2);

        return Promise.all(promises);
      } else if (status === "provider_work_completed") {
        const notifTime = admin.firestore.FieldValue.serverTimestamp();
        const notifText = newValue.service_provider_name +
                          " has completed your work." +
                          " Please continue to approve work completion.";
        const promises = [];

        const p = db.collection("Adme_User/"+clinetId+"/notification_list")
            .add({
              text: notifText,
              time: notifTime,
              mode: "client",
              type: "quotation",
              reference: appointmentId,
              img_url: spPic,
              isSeen: false,
              pushable: true,

            });

        promises.push(p);

        const p2 = db.collection("Adme_User").doc(clinetId)
            .set({
              hasUnreadNotifClient: true,

            }, {merge: true});
        promises.push(p2);

        return Promise.all(promises);
      } else if (status === "client_completion_approve") {
        const notifTime = admin.firestore.FieldValue.serverTimestamp();
        const notifText = newValue.client_name +
                          " approved your work completion." +
                          " Please continue to send invoice.";
        const promises = [];

        const p = db.collection("Adme_User/"+spId+"/notification_list")
            .add({
              text: notifText,
              time: notifTime,
              mode: "service_provider",
              type: "quotation",
              reference: appointmentId,
              img_url: clientPic,
              isSeen: false,
              pushable: true,

            });

        promises.push(p);

        const p2 = db.collection("Adme_User").doc(spId)
            .set({
              hasUnreadNotifSP: true,

            }, {merge: true});
        promises.push(p2);


        return Promise.all(promises);
      } else if (status === "provider_receipt_sent") {
        const notifTime = admin.firestore.FieldValue.serverTimestamp();
        const notifText = newValue.service_provider_name +
                          " has sent invoice." +
                          " Please pay to your service provider.";
        const promises = [];

        const p = db.collection("Adme_User/"+clinetId+"/notification_list")
            .add({
              text: notifText,
              time: notifTime,
              mode: "client",
              type: "quotation",
              reference: appointmentId,
              img_url: spPic,
              isSeen: false,
              pushable: true,

            });

        promises.push(p);

        const p2 = db.collection("Adme_User").doc(clinetId)
            .set({
              hasUnreadNotifClient: true,

            }, {merge: true});
        promises.push(p2);

        return Promise.all(promises);
      } else if (status === "payment_completed") {
        const notifTime = admin.firestore.FieldValue.serverTimestamp();
        const notifText = newValue.service_provider_name +
                          " has received your payment." +
                          " Please rate your service";
        const today = new Date();
        const year = today.getFullYear();
        const month = monthNames[today.getMonth()];
        // const incomeRef = year + "-" + month;
        const promises = [];
        const due = totalIncome * duePercentage;

        const p1 = db.collection("Adme_User/"+clinetId+"/notification_list")
            .add({
              text: notifText,
              time: notifTime,
              mode: "client",
              type: "quotation",
              reference: appointmentId,
              img_url: spPic,
              isSeen: false,
              pushable: true,

            });
        promises.push(p1);


        const ref = "/data/service_provider/income_history/monthly_income/";
        const p2 = db.collection("Adme_User/"+spId+ref+year)
            .doc(month)
            .set({
              monthly_income: admin.firestore.FieldValue.increment(totalIncome),
              monthly_due: admin.firestore.FieldValue.increment(due),
              due_paid: false,

            }, {merge: true});

        promises.push(p2);

        const p3 = db.collection("Adme_User/"+spId+"/data")
            .doc("service_provider")
            .update({
              completed: admin.firestore.FieldValue.increment(1),
              total_income: admin.firestore.FieldValue.increment(totalIncome),
            });

        promises.push(p3);


        const p4 = db.collection("Adme_User").doc(clinetId)
            .set({
              hasUnreadNotifClient: true,

            }, {merge: true});
        promises.push(p4);

        return Promise.all(promises);
      } else if (status === "client_request_canceled") {
        // delete appointment
        const promises = [];
        const notifTextSp ="Your Quotation has been canceled by " +
                              newValue.client_name;
        const notifTextClient = "Your Quotation has been canceled. " +
                          "Please Visit Service Provider profile "+
                          " to send Quotation again";
        const notifTime = admin.firestore.FieldValue.serverTimestamp();
        // const p1 = db
        //     .collection("Adme_Appointment_list")
        //     .doc(appointmentId)
        //     .delete();
        // promises.push(p1);

        db.collection("Adme_User/"+spId+"/notification_list")
            .where("reference", "==", appointmentId)
            .get().then((snapshot) => {
              snapshot.forEach((doc) => {
                const p = doc.ref.delete();
                promises.push(p);
              });
            })
            .catch((err) => {
              console.log("Error getting documents", err);
            });

        db.collection("Adme_User/"+clinetId+"/notification_list")
            .where("reference", "==", appointmentId)
            .get().then((snapshot) => {
              snapshot.forEach((doc) => {
                const p = doc.ref.delete();
                promises.push(p);
              });
            })
            .catch((err) => {
              console.log("Error getting documents", err);
            });

        const p2 = db.collection("Adme_User/"+spId+"/notification_list").add({
          text: notifTextSp,
          time: notifTime,
          mode: "service_provider",
          type: "quotation",
          reference: appointmentId,
          img_url: clientPic,
          isSeen: false,
          pushable: true,

        });

        promises.push(p2);

        const p3 = db.collection("Adme_User/"+clinetId+"/notification_list")
            .add({
              text: notifTextClient,
              time: notifTime,
              mode: "client",
              type: "profile",
              reference: spId,
              img_url: spPic,
              isSeen: false,
              pushable: false,

            });

        promises.push(p3);

        const p4 = db.collection("Adme_User").doc(spId)
            .set({
              hasUnreadNotifSP: true,

            }, {merge: true});
        promises.push(p4);

        const p5 = db.collection("Adme_User").doc(clinetId)
            .set({
              hasUnreadNotifClient: true,

            }, {merge: true});
        promises.push(p5);

        return Promise.all(promises);
      } else if (status === "provider_request_cancel") {
        // delete appointment
        const promises = [];
        const notifTextSp ="Your Quotation with" +
                          newValue.client_name +" has been canceled";
        const notifTextClient = "Your Quotation has been canceled. " +
                          "Please Visit Service Provider profile "+
                          " to send Quotation again";
        const notifTime = admin.firestore.FieldValue.serverTimestamp();
        // const p1 = db
        //     .collection("Adme_Appointment_list")
        //     .doc(appointmentId)
        //     .delete();
        // promises.push(p1);

        db.collection("Adme_User/"+spId+"/notification_list")
            .where("reference", "==", appointmentId)
            .get().then((snapshot) => {
              snapshot.forEach((doc) => {
                const p = doc.ref.delete();
                promises.push(p);
              });
            })
            .catch((err) => {
              console.log("Error getting documents", err);
            });

        db.collection("Adme_User/"+clinetId+"/notification_list")
            .where("reference", "==", appointmentId)
            .get().then((snapshot) => {
              snapshot.forEach((doc) => {
                const p = doc.ref.delete();
                promises.push(p);
              });
            })
            .catch((err) => {
              console.log("Error getting documents", err);
            });

        const p2 = db.collection("Adme_User/"+spId+"/notification_list").add({
          text: notifTextSp,
          time: notifTime,
          mode: "service_provider",
          type: "quotation",
          reference: appointmentId,
          img_url: clientPic,
          isSeen: false,
          pushable: true,

        });

        promises.push(p2);

        const p3 = db.collection("Adme_User/"+clinetId+"/notification_list")
            .add({
              text: notifTextClient,
              time: notifTime,
              mode: "client",
              type: "profile",
              reference: spId,
              img_url: spPic,
              isSeen: false,
              pushable: false,

            });

        promises.push(p3);

        const p4 = db.collection("Adme_User").doc(spId)
            .set({
              hasUnreadNotifSP: true,

            }, {merge: true});
        promises.push(p4);

        const p5 = db.collection("Adme_User").doc(clinetId)
            .set({
              hasUnreadNotifClient: true,

            }, {merge: true});
        promises.push(p5);

        return Promise.all(promises);
      } else if (status === "provider_response_decline") {
        // client declines. send notification to service provider
        const promises = [];
        const notifTextSp =newValue.client_name +
                            "Declined your Quotation response";
        // const notifTextClient = "Your Quotation has been canceled. " +
        //                   "Please Visit Service Provider profile "+
        //                   " to send Quotation again";
        const notifTime = admin.firestore.FieldValue.serverTimestamp();
        const p2 = db.collection("Adme_User/"+spId+"/notification_list").add({
          text: notifTextSp,
          time: notifTime,
          mode: "service_provider",
          type: "quotation",
          reference: appointmentId,
          img_url: clientPic,
          isSeen: false,
          pushable: true,

        });

        promises.push(p2);

        return Promise.all(promises);
      } else if (status === "client_completion_denied") {
        // client declines. send notification to service provider
        const promises = [];
        const notifTextSp =newValue.client_name +
                            "Declined your service completion";
        // const notifTextClient = "Your Quotation has been canceled. " +
        //                   "Please Visit Service Provider profile "+
        //                   " to send Quotation again";
        const notifTime = admin.firestore.FieldValue.serverTimestamp();
        const p2 = db.collection("Adme_User/"+spId+"/notification_list").add({
          text: notifTextSp,
          time: notifTime,
          mode: "service_provider",
          type: "quotation",
          reference: appointmentId,
          img_url: clientPic,
          isSeen: false,
          pushable: true,

        });

        promises.push(p2);

        return Promise.all(promises);
      } else {
        return null;
      }
    });


// update FCM token
exports.updateFCMToken = functions.https.onCall((data, context) => {
  const token = data.token;
  const userId = data.user_id;
  const userRef = db.collection("Adme_User").doc(userId);
  const response = {};

  return userRef.get().then((document) => {
    if (document.exists) {
      // document exist
      let tokens = [];
      // check fcm_token is defined or not
      if (document.get("fcm_token") === undefined) {
        // fcm_token field doesn't exist
        tokens.push(token);
      } else {
        // fcm_token field exit and fetch the array
        tokens = document.data().fcm_token;
        tokens.push(token);
      }
      return userRef.set({
        fcm_token: tokens,

      }, {merge: true})
          .then((res) => {
            response.message = "success";
            return {
              data: JSON.stringify(response),
            };
          })
          .catch((err) => {
            response.message = "failed";
            return {
              data: JSON.stringify(response),
            };
          });
    } else {
      console.log("No matching documents.");
      throw new functions.https.HttpsError("Error", "Found Empty Data");
    }
  });
});

// send push notification on notification creation
exports.triggerPush = functions.firestore
    .document("Adme_User/{userId}/notification_list/{notifId}")
    .onCreate((snap, context) => {
      const notification = snap.data();
      const pushable = notification.pushable;
      const body = notification.text;
      const reference = notification.reference;
      const UserId = context.params.userId;
      const userRef = db.collection("Adme_User").doc(UserId);
      if (pushable) {
        // send notification
        return userRef.get().then((document) => {
          if (document.exists) {
            // document exist
            const tokens = document.data().fcm_token;
            const newToken = [];
            const message = {
              data: {
                title: "Quotation Update",
                body: body,
                reference: reference,
              },
              tokens: tokens,
            };

            admin.messaging().sendMulticast(message)
                .then((response) => {
                  if (response.failureCount > 0) {
                    // const failedTokens = [];
                    response.responses.forEach((resp, idx) => {
                      if (resp.success) {
                        // failedTokens.push(tokens[idx]);
                        newToken.push(tokens[idx]);
                      }
                    });
                    // delete invalid tokens
                    return userRef.set({
                      fcm_token: newToken,

                    }, {merge: true});
                  }
                });
          } else {
            console.log("No matching documents.");
            return;
          }
        });
      } else {
        return;
      }
    });

/** Get Nearby services */
exports.getNearbyServices = functions.https.onCall((data, context) => {
  const serviceRef = db.collection("Adme_Service_list");
  const docs = [];
  const finalJson = [];
  const lattitude = data.latitude;
  const longitude = data.longitude;
  // const fial_jsn = [];
  console.log(data);
  return serviceRef.get().then((snapshot) => {
    if (snapshot.empty) {
      console.log("No matching documents.");
      throw new functions.https.HttpsError("Error", "Found Empty Data");
    } else {
      console.log("Found matching documents.");
      snapshot.forEach((doc) => {
        const obj = {
          "id": doc.id,
          "category": doc.data().category,
          "categoryId": doc.data().categoryId,
          "description": doc.data().description,
          "latitude": doc.data().latitude,
          "longitude": doc.data().longitude,
          "max_charge": doc.data().max_charge,
          "min_charge": doc.data().min_charge,
          "pic_url": doc.data().pic_url,
          "rating": doc.data().rating,
          "reviews": doc.data().reviews,
          "user_name": doc.data().user_name,
          "tags": doc.data().tags,
          "user_ref": doc.data().user_ref,
        };
        // var data = doc.id
        console.log(doc.data().latitude);
        console.log(doc.data().longitude);
        const serviceLat = parseFloat(doc.data().latitude);
        const serviceLong = parseFloat(doc.data().longitude);
        if (isNearByService(lattitude,
            longitude, serviceLat, serviceLong)) {
          docs.push(obj);
        }
      });

      const jsn = JSON.parse(JSON.stringify(docs));
      console.log("Json : ");
      console.log(jsn);
      jsn.forEach((doc) => {
        const res = doc;
        delete res["tags"];
        finalJson.push(res);
      });


      return {
        data: JSON.stringify(docs),
      };
    }
  });
});


function isNearByService(userLattitude,
    userLongitude, serviceLattitude, serviceLongitude) {
  const point1 = new GeoPoint(userLattitude, userLongitude);
  const point2 = new GeoPoint(serviceLattitude, serviceLongitude);
  const distance = point1.distanceTo(point2, true);
  console.log(distance);
  return distance < 5 ? true : false;
}

/** Update Rating Review */
exports.updateRatingReview = functions.firestore
    .document("Adme_User/{userId}/data/service_provider"+
    "/services/{serviceId}/reviews/{reviewId}")
    .onCreate((snap, context) => {
      // Get an object representing the document
      // e.g. {'name': 'Marie', 'age': 66}

      const promises = [];
      const review = snap.data();
      const serviceId = context.params.serviceId;
      const UserId = context.params.userId;
      // const reviewId = context.params.reviewId;

      const p1 = db.collection("Adme_User/"+
          UserId+"/data/service_provider/services")
          .doc(serviceId)
          .get()
          .then((document) => {
            if (document.exists) {
              let updatedRrated = 1;
              let updatedRating = review.rating;
              if (document.get("rated") !== undefined &&
                  document.get("rating") !== undefined) {
                // average rating
                const prevRated = document.data().rated;
                const prevRating = document.data().rating;

                updatedRating = ((prevRated*prevRating) +
                updatedRating)/(prevRated + 1);

                updatedRrated = prevRated + 1;
              }

              // update rating
              return db.collection("Adme_User/"+
                  UserId+"/data/service_provider/services")
                  .doc(serviceId)
                  .set({
                    rated: updatedRrated,
                    rating: updatedRating,

                  }, {merge: true});
            } else {
              console.log("No matching documents.");
              throw new functions.https.HttpsError("Error", "Found Empty Data");
            }
          });


      promises.push(p1);

      const p2 = db.collection("Adme_User/"+
          UserId+"/data")
          .doc("service_provider")
          .get()
          .then((document) => {
            if (document.exists) {
              let updatedRrated = 1;
              let updatedRating = review.rating;
              if (document.get("rated") !== undefined &&
                  document.get("rating") !== undefined) {
                // average rating
                const prevRated = document.data().rated;
                const prevRating = document.data().rating;

                updatedRating = ((prevRated*prevRating) +
                updatedRating)/(prevRated + 1);

                updatedRrated = prevRated + 1;
              }

              // update rating
              return db.collection("Adme_User/"+
                  UserId+"/data")
                  .doc("service_provider")
                  .set({
                    rated: updatedRrated,
                    rating: updatedRating,

                  }, {merge: true});
            } else {
              console.log("No matching documents.");
              throw new functions.https.HttpsError("Error", "Found Empty Data");
            }
          });
      promises.push(p2);

      return Promise.all(promises);
    });

