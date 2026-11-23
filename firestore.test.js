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

function buildUserProgressPayload(uid, overrides = {}) {
  const past = new Date(Date.now() - 5000);
  return {
    userId: uid,
    displayName: "زياد رمضان",
    totalXp: 350,
    level: 3,
    totalAnswered: 20,
    totalCorrect: 18,
    completedQuizzes: 4,
    highestScorePercent: 100,
    bestSurvivalStreak: 12,
    dailyStreak: 2,
    unlockedBadgeIds: ["first_step", "perfectionist"],
    createdAt: past,
    updatedAt: past,
    ...overrides,
  };
}

function buildLeaderboardPayload(uid, overrides = {}) {
  const past = new Date(Date.now() - 5000);
  return {
    userId: uid,
    displayName: "زياد رمضان",
    totalXp: 350,
    level: 3,
    levelTitle: "باحث في المعرفة",
    bestSurvivalStreak: 12,
    unlockedBadgesCount: 2,
    createdAt: past,
    updatedAt: past,
    ...overrides,
  };
}

test("Unauthenticated user cannot read or write users or leaderboard", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).set(buildUserProgressPayload(ALICE_UID))
  );
  await assertFails(
    unauthDb.collection("leaderboard").where("totalXp", ">=", 0).orderBy("totalXp", "desc").get()
  );
  await assertFails(
    unauthDb.collection("leaderboard").doc(ALICE_UID).set(buildLeaderboardPayload(ALICE_UID))
  );
});

test("Authenticated owner can create and partially update own user progress; rejects ghost/immutable/cross-user writes", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(buildUserProgressPayload(ALICE_UID))
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      totalXp: 500,
      level: 4,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject immutable createdAt mutation
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject shadow/ghost field
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set(
      buildUserProgressPayload(ALICE_UID, { isAdmin: true })
    )
  );

  // Reject cross-user read and write
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).update({
      totalXp: 9999,
      updatedAt: new Date(Date.now() - 1000),
    })
  );
  await assertFails(
    bobDb.collection("users").doc(BOB_UID).set(buildUserProgressPayload(ALICE_UID))
  );
});

test("Leaderboard allows owner writes, rejects spoofing/ghost fields/negative XP, and allows signed-in filtered query", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(
    aliceDb.collection("leaderboard").doc(ALICE_UID).set(buildLeaderboardPayload(ALICE_UID))
  );

  await assertSucceeds(
    aliceDb.collection("leaderboard").doc(ALICE_UID).update({
      totalXp: 600,
      level: 4,
      levelTitle: "محب للعلم",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Matches Kotlin query: whereGreaterThanOrEqualTo("totalXp", 0).orderBy("totalXp", DESC).limit(25)
  await assertSucceeds(
    bobDb.collection("leaderboard")
      .where("totalXp", ">=", 0)
      .orderBy("totalXp", "desc")
      .limit(25)
      .get()
  );

  // Reject unfiltered list query (since rule requires totalXp >= 0)
  await assertFails(bobDb.collection("leaderboard").get());

  // Reject cross-user overwrite
  await assertFails(
    bobDb.collection("leaderboard").doc(ALICE_UID).update({
      totalXp: 1,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject negative XP or future timestamp
  await assertFails(
    bobDb.collection("leaderboard").doc(BOB_UID).set(
      buildLeaderboardPayload(BOB_UID, { totalXp: -10 })
    )
  );
  await assertFails(
    bobDb.collection("leaderboard").doc(BOB_UID).set(
      buildLeaderboardPayload(BOB_UID, {
        createdAt: new Date(Date.now() + 60000),
        updatedAt: new Date(Date.now() + 60000),
      })
    )
  );
});
