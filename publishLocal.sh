echo ====================================
echo "publishing frex-common"
echo ====================================
cd common
../gradlew publishToMavenLocal --rerun-tasks
cd ..

echo ====================================
echo "publishing frex-fabric"
echo ====================================
cd fabric
../gradlew publishToMavenLocal --rerun-tasks
cd ..

echo ====================================
echo "publishing frex distribution"
echo ====================================
./gradlew publishToMavenLocal --rerun-tasks
