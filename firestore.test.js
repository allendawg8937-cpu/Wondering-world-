const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read or write user profiles", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: can create and read their own profile with valid schema", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Jordan Vance",
      role: "veteran",
      age: 26,
      location: "Austin, TX",
      bio: "We are all equal.",
      themeColorHex: "#6366F1",
      musicTrackTitle: "Comfortably Numb - Pink Floyd",
      musicPlatform: "Spotify",
      savingsBalanceCents: 245075,
      savingsGoalCents: 500000,
      arcadeHighScore: 120,
      pledgeAccepted: true,
      interests: ["Healthcare", "Music"],
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: cannot read another user's profile (cross-user isolation)", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    const now = new Date();
    await context.firestore().collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      displayName: "Bob",
      role: "adult",
      age: 30,
      location: "NYC",
      bio: "Hello",
      themeColorHex: "#10B981",
      musicTrackTitle: "Song",
      musicPlatform: "Spotify",
      savingsBalanceCents: 1000,
      savingsGoalCents: 5000,
      arcadeHighScore: 0,
      pledgeAccepted: true,
      interests: [],
      createdAt: now,
      updatedAt: now,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
});

test("Shadow update test: rejects ghost fields on profile update", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Jordan Vance",
      role: "veteran",
      age: 26,
      location: "Austin, TX",
      bio: "We are all equal.",
      themeColorHex: "#6366F1",
      musicTrackTitle: "Comfortably Numb",
      musicPlatform: "Spotify",
      savingsBalanceCents: 245075,
      savingsGoalCents: 500000,
      arcadeHighScore: 120,
      pledgeAccepted: true,
      interests: ["Healthcare"],
      createdAt: now,
      updatedAt: now,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      displayName: "Hacked Name",
      isAdmin: true,
      updatedAt: new Date(),
    })
  );
});

test("Authenticated user: can create and query own wall posts with userId filter", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("posts").doc("post_1").set({
      userId: ALICE_UID,
      authorName: "Jordan Vance",
      authorRole: "veteran",
      category: "Resource Guide",
      title: "VA Healthcare Guide",
      content: "Step-by-step guide to scheduling appointments and accessing benefits.",
      likesCount: 5,
      accentHex: "#6366F1",
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("posts")
      .where("userId", "==", ALICE_UID)
      .get()
  );
});

test("Authenticated user: can create, update, and query own career milestones, and blocks cross-user/unauth access", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  const milestoneRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("career_milestones")
    .doc("milestone_1");

  await assertSucceeds(
    milestoneRef.set({
      userId: ALICE_UID,
      title: "Complete FAFSA & Federal Student Aid",
      stage: "Foundation",
      description: "Submit FAFSA application on studentaid.gov for Pell Grants and scholarships.",
      targetTerm: "Step 1 • Fall Term",
      status: "In Progress",
      xpReward: 150,
      stepOrder: 1,
      portalUrl: "https://studentaid.gov/",
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    milestoneRef.update({
      status: "Completed",
      updatedAt: new Date(),
    })
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("career_milestones")
      .where("userId", "==", ALICE_UID)
      .get()
  );

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("career_milestones")
      .doc("milestone_1")
      .get()
  );

  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("career_milestones")
      .doc("milestone_1")
      .get()
  );
});
