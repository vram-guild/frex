echo ====================================
echo "publishing frex-core"
echo ====================================
cd core
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
