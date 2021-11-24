echo 'Checking for build updates...'
# delete gruntle repo folder if exists from aborted run
if [ -d "gruntle-master" ]; then
  rm -rf gruntle-master
fi

curl https://github.com/vram-guild/gruntle/archive/refs/heads/master.zip -O -J -L
unzip -q gruntle-master
cp -R gruntle-master/1.17/ .
rm -rf gruntle-master
rm gruntle-master.zip

source gruntle/refresh_scripts.sh
#source script/compute_version.sh

rm -rf gruntle

# download checkstyle
# update MC/Fabric versions
# get latest build.gradle
# get latest gradle.settings

echo 'ALL DONE'
