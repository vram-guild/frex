echo ====================================
echo "publishing frex-common"
echo ====================================
cd common
../gradlew publish --rerun-tasks
cd ..

echo ====================================
echo "publishing frex-fabric"
echo ====================================
cd fabric
../gradlew publish --rerun-tasks
cd ..

echo ====================================
echo "publishing frex distribution"
echo ====================================
./gradlew publish --rerun-tasks
