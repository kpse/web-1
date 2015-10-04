#!/bin/sh

#npm install yo
#npm install coffee-script
#npm install coffee-script-redux
#npm install karma
#npm install generator-angular
#npm install phantomjs
#sudo npm install -g PhantomJS
#npm install karma-phantomjs-launcher --save-dev

function load_env {
  [ -f ./kulebao_config/dev_env.sh ] && source ./kulebao_config/dev_env.sh
}

function build_local {
    load_env
    JAVA_OPTS=-Xmx4g node_modules/karma/bin/karma start --single-run && \
    play -Dlogger.resource=travis-logger.xml -Dconfig.resource=test.conf pmd checkstyle findbugs test
}

function build_and_push {
    git submodule update &&
    git pull --rebase && \
    build_local && \
    git push origin master
}

function deploy_heroku {
    build_local && \
    git push heroku master
}

function local_https_server {
    load_env
    JAVA_OPTS="-Dhttps.port=9001 -Xmx3g" play h2-browser run
}

function local_https_server_with_prod_logger {
    load_env
    JAVA_OPTS="-Dhttps.port=9001 -Xmx3g -Dlogger.resource=$(pwd)/conf/prod-logger.xml" play h2-browser run
}

function local_https_server_with_minjs {
    load_env
    grunt minjs &&
    JAVA_OPTS="-Dhttps.port=9001 -Xmx3g" play -Dassets.min=true h2-browser run
}

function local_https_server_with_mysql {
    load_env
    JAVA_OPTS="-Dhttps.port=9001 -Xmx3g -Dconfig.resource=application_mysql.conf" play run
}

function deploy_prod {
    echo ".... start to deploy on env $1 ..."
    now=$(date +"%s")
    srcFilename="$(pwd)/target/universal/kulebao-1.0-SNAPSHOT.zip"
    destFilename="kulebao-1.0-SNAPSHOT.$now.zip"
    destServer="kulebao@$1"
    destPath="$destServer:~/$destFilename"
    grunt minjs && play dist && \
    scp $srcFilename $destPath && \
    ssh $destServer "unzip -x $destFilename -d /var/play/$now/" && \
    ssh $destServer "rm /var/play/kulebao" && \
    ssh $destServer "ln -s /var/play/$now/kulebao-1.0-SNAPSHOT/ /var/play/kulebao" && \
    ssh $destServer "echo coco999 | sudo -S service kulebao restart"

    retvalue=$?
    echo "Return value: $retvalue"
    echo "Done deployment $1"
}


function build_deploy_stage {
  build_and_push && deploy_prod stage.cocobabys.com
}

function js_dependency {
  grunt
}

function usage {
  echo Usage:
  echo ======================
  echo s for start local development server at 9000 port
  echo a for git pull/local build/git push/deploy to stage
  echo d for deploy to stage.cocobabys.com
  echo p for git pull/local build/git push
  echo b for local build
  echo js for update javascript dependency
  echo heroku for deploy to heroku
  echo none of the above will trigger local build only
  echo ======================
}

function main {
  	case $1 in
		js) js_dependency ;;
		s) local_https_server ;;
		ss) local_https_server_with_minjs ;;
		mysql) local_https_server_with_mysql ;;
		sp) local_https_server_with_prod_logger ;;
		a) build_deploy_stage ;;
		heroku) deploy_heroku ;;
		prod) deploy_prod cocobabys.com ;;
		d) deploy_prod stage.cocobabys.com ;;
		p) build_and_push ;;
		b) build_local ;;
		h) usage ;;
		*) build_local ;;
	esac
}

main $@
