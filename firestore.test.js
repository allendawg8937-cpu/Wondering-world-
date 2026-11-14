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
      themeVisualStyle: "HYPERCUBE_6D",
      isDarkMode: true,
      musicTrackTitle: "Comfortably Numb - Pink Floyd",
      musicPlatform: "Spotify",
      savingsBalanceCents: 245075,
      savingsGoalCents: 500000,
      arcadeHighScore: 120,
      pledgeAccepted: true,
      phoneNumber: "555-0100",
      interests: ["Healthcare", "Music"],
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: cannot update another user's profile (cross-user write isolation)", async () => {
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
      themeVisualStyle: "HYPERCUBE_6D",
      isDarkMode: true,
      musicTrackTitle: "Song",
      musicPlatform: "Spotify",
      savingsBalanceCents: 1000,
      savingsGoalCents: 5000,
      arcadeHighScore: 0,
      pledgeAccepted: true,
      phoneNumber: "555-0199",
      interests: [],
      createdAt: now,
      updatedAt: now,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).update({
      displayName: "Tampered Bob",
      updatedAt: new Date(),
    })
  );
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
      themeVisualStyle: "HYPERCUBE_6D",
      isDarkMode: true,
      musicTrackTitle: "Comfortably Numb",
      musicPlatform: "Spotify",
      savingsBalanceCents: 245075,
      savingsGoalCents: 500000,
      arcadeHighScore: 120,
      pledgeAccepted: true,
      phoneNumber: "555-0100",
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

test("Profile3DConfig: authenticated owner can create, read, list, update, and delete; blocks cross-user, unauth, ghost fields, and invalid schemas", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const now = new Date();

  const aliceConfigRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("profile_3d_configs")
    .doc("active");

  const validConfig = {
    userId: ALICE_UID,
    configName: "Starlight Vanguard",
    baseModelId: "cyber_astronaut",
    baseModelName: "Cyber Astronaut",
    primaryColorHex: "#00E5FF",
    secondaryColorHex: "#FF2E93",
    auraColorHex: "#00E676",
    headgearId: "head_crown",
    eyewearId: "eye_visor",
    auraId: "aura_drones",
    rigId: "rig_pauldrons",
    metallicFinish: 0.75,
    scaleFactor: 1.05,
    autoRotate: true,
    turntableYawDeg: 18.0,
    isActive: true,
    createdAt: now,
    updatedAt: now,
  };

  // Owner create & read & list
  await assertSucceeds(aliceConfigRef.set(validConfig));
  await assertSucceeds(aliceConfigRef.get());
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("profile_3d_configs")
      .where("userId", "==", ALICE_UID)
      .get()
  );

  // Owner valid update (preserves createdAt and userId)
  await assertSucceeds(
    aliceConfigRef.update({
      baseModelId: "holo_scholar",
      baseModelName: "Holo Scholar",
      primaryColorHex: "#8B5CF6",
      headgearId: "head_halo",
      metallicFinish: 0.9,
      updatedAt: new Date(),
    })
  );

  // Reject ghost field shadow update
  await assertFails(
    aliceConfigRef.update({
      configName: "Hacked Config",
      isAdmin: true,
      updatedAt: new Date(),
    })
  );

  // Reject invalid baseModelId or invalid hex color
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("profile_3d_configs")
      .doc("bad_model")
      .set({
        ...validConfig,
        baseModelId: "hacked_model",
      })
  );

  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("profile_3d_configs")
      .doc("bad_hex")
      .set({
        ...validConfig,
        primaryColorHex: "not-a-hex",
      })
  );

  // Cross-user isolation (Bob cannot read, list, write, or delete Alice's 3D configs)
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("profile_3d_configs")
      .doc("active")
      .get()
  );
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("profile_3d_configs")
      .doc("active")
      .update({ configName: "Bob Hijack", updatedAt: new Date() })
  );
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("profile_3d_configs")
      .doc("active")
      .delete()
  );

  // Unauthenticated access denied
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("profile_3d_configs")
      .doc("active")
      .get()
  );
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("profile_3d_configs")
      .doc("unauth_doc")
      .set(validConfig)
  );

  // Owner delete succeeds
  await assertSucceeds(aliceConfigRef.delete());
});

test("PersonaStageData: authenticated owner can create, read, list, update, and delete; blocks invalid stageId, mismatch, ghost fields, cross-user, and unauth access", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const now = new Date();

  const aliceVeteranRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("persona_stage_data")
    .doc("veteran");

  const validStageData = {
    userId: ALICE_UID,
    stageId: "veteran",
    stageLabel: "Veteran",
    focusGoal: "Complete PACT Act Screening & GI Bill Housing Verification",
    primaryMetricLabel: "Transition Steps Done",
    primaryMetricValue: 3,
    primaryMetricTarget: 6,
    pinnedTopics: ["PACT Act & VA Health", "Post-9/11 GI Bill"],
    completedTaskIds: ["vet_task_1"],
    customNote: "Connected with local DAV peer mentor.",
    stageScoreOrXp: 450,
    createdAt: now,
    updatedAt: now,
  };

  // Owner create, read, list
  await assertSucceeds(aliceVeteranRef.set(validStageData));
  await assertSucceeds(aliceVeteranRef.get());
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("persona_stage_data")
      .where("userId", "==", ALICE_UID)
      .get()
  );

  // Owner valid update
  await assertSucceeds(
    aliceVeteranRef.update({
      focusGoal: "Schedule VA Clinic Follow-up",
      primaryMetricValue: 4,
      completedTaskIds: ["vet_task_1", "vet_task_2"],
      stageScoreOrXp: 490,
      updatedAt: new Date(),
    })
  );

  // Reject ghost field shadow update
  await assertFails(
    aliceVeteranRef.update({
      focusGoal: "Hijack",
      isAdmin: true,
      updatedAt: new Date(),
    })
  );

  // Reject invalid stageId document ID
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("persona_stage_data")
      .doc("admin")
      .set({
        ...validStageData,
        stageId: "admin",
      })
  );

  // Reject stageId mismatch between doc ID and payload
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("persona_stage_data")
      .doc("kid")
      .set({
        ...validStageData,
        stageId: "veteran",
      })
  );

  // Cross-user isolation (Bob cannot read, write, or delete Alice's stage data)
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("persona_stage_data")
      .doc("veteran")
      .get()
  );
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("persona_stage_data")
      .doc("veteran")
      .update({ focusGoal: "Bob Tamper", updatedAt: new Date() })
  );
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("persona_stage_data")
      .doc("veteran")
      .delete()
  );

  // Unauthenticated access denied
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("persona_stage_data")
      .doc("veteran")
      .get()
  );
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("persona_stage_data")
      .doc("kid")
      .set({
        ...validStageData,
        stageId: "kid",
        stageLabel: "Kid",
      })
  );

  // Owner delete succeeds
  await assertSucceeds(aliceVeteranRef.delete());
});

test("BlockedUsers: authenticated owner can block, list, and unblock; blocks self-block, ghost fields, update, cross-user, and unauth access", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const now = new Date();

  const aliceBlockBobRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("blocked_users")
    .doc(BOB_UID);

  const validBlock = {
    userId: ALICE_UID,
    blockedUserId: BOB_UID,
    blockedDisplayName: "Bob Builder",
    reason: "Harassment",
    createdAt: now,
  };

  // Owner create, read, list
  await assertSucceeds(aliceBlockBobRef.set(validBlock));
  await assertSucceeds(aliceBlockBobRef.get());
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("blocked_users")
      .get()
  );

  // Reject updates (must delete/unblock instead)
  await assertFails(aliceBlockBobRef.update({ reason: "Changed" }));

  // Reject self-blocking
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("blocked_users")
      .doc(ALICE_UID)
      .set({
        userId: ALICE_UID,
        blockedUserId: ALICE_UID,
        blockedDisplayName: "Alice",
        reason: "Self",
        createdAt: now,
      })
  );

  // Reject ghost fields
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("blocked_users")
      .doc("charlie_789")
      .set({
        userId: ALICE_UID,
        blockedUserId: "charlie_789",
        blockedDisplayName: "Charlie",
        reason: "Spam",
        isAdminBlock: true,
        createdAt: now,
      })
  );

  // Cross-user isolation: Bob cannot read, write, or remove Alice's blocklist
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("blocked_users")
      .doc(BOB_UID)
      .get()
  );
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("blocked_users")
      .doc(BOB_UID)
      .delete()
  );

  // Unauthenticated denied
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("blocked_users")
      .doc(BOB_UID)
      .get()
  );

  // Owner unblock (delete) succeeds
  await assertSucceeds(aliceBlockBobRef.delete());
});

test("User Profile Delete: owner can delete own user document, cross-user and unauth cannot", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const now = new Date();

  await aliceDb.collection("users").doc(ALICE_UID).set({
    userId: ALICE_UID,
    displayName: "Alice",
    role: "adult",
    age: 28,
    location: "Austin, TX",
    bio: "Equal community member",
    themeColorHex: "#00E5FF",
    themeVisualStyle: "HYPERCUBE_6D",
    isDarkMode: true,
    musicTrackTitle: "Track",
    musicPlatform: "Spotify",
    savingsBalanceCents: 1000,
    savingsGoalCents: 5000,
    arcadeHighScore: 0,
    pledgeAccepted: true,
    phoneNumber: "555-0100",
    interests: ["Community"],
    createdAt: now,
    updatedAt: now,
  });

  await assertFails(bobDb.collection("users").doc(ALICE_UID).delete());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).delete());
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).delete());
});



