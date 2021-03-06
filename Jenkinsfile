properties([
    // Job parameter defintions.
    parameters([
        choiceParam(
            description: 'Release or Dev branch to build',
            name: 'Branch',
            choices: 'branch-3.0\nbranch-2.8\nmaster'
        ),
        choiceParam(
            description: 'Avaliable platforms to build off of',
            name: 'Platform',
            choices: 'cdh5.12.0\ncdh5.12.2\ncdh5.12.2-2.3\ncdh5.13.3\ncdh5.14.0\ncdh5.14.2\ncdh5.16.1\ncdh6.3.0\nhdp2.6.1\nhdp2.6.3\nhdp2.6.4\nhdp2.6.5\nhdp3.1.0'
        ),
        stringParam(
            description: 'Tag to build off of i.e. 3.0.0.1960',
            name: 'Tag',
            defaultValue: '3.0.0.1960'
        )
    ])
])

def vault_addr="https://vault.build.splicemachine-dev.io"
def branch = ""
def source_branch = "${Branch}"
def source_folder = "."
def splice_version = "${Tag}"
def env_classifier = "${Platform}"
def hbase_version = ""
def hadoop_version = ""
def kafka_version = ""
def spark_folder = ""

// Launch the docker container
node('splice-standalone') {
    def artifact_values  = [
        [$class: 'VaultSecret', path: "secret/aws/jenkins/colo_jenkins", secretValues: [
            [$class: 'VaultSecretValue', envVar: 'ARTIFACT_USER', vaultKey: 'user'],
            [$class: 'VaultSecretValue', envVar: 'ARTIFACT_PASSWORD', vaultKey: 'pass']]]
    ]

    try {

    notifyBuild('STARTED')
    echo source_branch

    stage('Checkout') {
      // Checkout code from repository
        checkout([  
            $class: 'GitSCM', 
            branches: [[name: splice_version]], 
            doGenerateSubmoduleConfigurations: false, 
            extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'spliceengine']], 
            submoduleCfg: [], 
            userRemoteConfigs: [[credentialsId: '88647ede-744a-444b-8c08-8313cc137944', url: 'https://github.com/splicemachine/spliceengine.git']]
        ])
        checkout([  
            $class: 'GitSCM', 
            branches: [[name: splice_version]],
            doGenerateSubmoduleConfigurations: false, 
            extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'spliceengine-ee']], 
            submoduleCfg: [], 
            userRemoteConfigs: [[credentialsId: '88647ede-744a-444b-8c08-8313cc137944', url: 'https://github.com/splicemachine/spliceengine-ee.git']]
        ])
        checkout([  
            $class: 'GitSCM', 
            branches: [[name: source_branch]],
            doGenerateSubmoduleConfigurations: false, 
            extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'splice-machine-spark-connector']], 
            submoduleCfg: [], 
            userRemoteConfigs: [[credentialsId: '88647ede-744a-444b-8c08-8313cc137944', url: 'https://github.com/splicemachine/splice-machine-spark-connector.git']]
        ])
    }
    stage('Build Dependencies') {
        wrap([$class: 'VaultBuildWrapper', vaultSecrets: artifact_values]) {
            dir('spliceengine'){
                def platforms = "$env_classifier"
                sh "mvn -B clean install -Pcore -DskipTests"
                sh '''
                cp pipelines/spot-bugs/template/settings.xml ~/.m2/settings.xml
                sed  -i "s/REPLACE_USER/$ARTIFACT_USER/" ~/.m2/settings.xml
                sed  -i "s/REPLACE_PASS/$ARTIFACT_PASSWORD/" ~/.m2/settings.xml
                '''
                sh "mvn -B clean install -Pmem,$platforms,ee -DskipTests"
                sh '''
                apt-get autoremove --purge scala -y
                wget www.scala-lang.org/files/archive/scala-2.11.8.deb
                dpkg -i scala-2.11.8.deb
                echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list
                apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823
                apt-get update
                apt-get install sbt -y
                '''
            }
        }
      }

    stage('Build SSDS') {
      dir('splice-machine-spark-connector'){
                if ("$env_classifier" == "cdh5.12.0"){
                    spark_folder = "."
                    hbase_version = "1.2.0-${envClassifier.value}"
                    hadoop_version = "2.6.0-${envClassifier.value}"
                    kafka_version = "0.10.0-kafka-2.1.0"
                } else if ("$env_classifier" == "cdh5.12.2"){
                    spark_folder = "."
                    hbase_version = "1.2.0-${envClassifier.value}"
                    hadoop_version = "2.6.0-${envClassifier.value}"
                    kafka_version = "0.10.0-kafka-2.1.0"
                } else if ("$env_classifier" == "cdh5.12.2-2.3"){
                    env_classifier = "cdh5.12.2"
                    spark_folder = "."
                    hbase_version = "1.2.0-${envClassifier.value}"
                    hadoop_version = "2.6.0-${envClassifier.value}"
                    kafka_version = "0.10.0-kafka-2.1.0"
                } else if ("$env_classifier" == "cdh5.13.3"){
                    spark_folder = "."
                    hbase_version = "1.2.0-${envClassifier.value}"
                    hadoop_version = "2.6.0-${envClassifier.value}"
                    kafka_version = "0.10.0-kafka-2.1.0"
                } else if ("$env_classifier" == "cdh5.14.0"){
                    spark_folder = "."
                    hbase_version = "1.2.0-${envClassifier.value}"
                    hadoop_version = "2.6.0-${envClassifier.value}"
                    kafka_version = "0.10.0-kafka-2.1.0"
                } else if ("$env_classifier" == "cdh5.14.2"){
                    spark_folder = "."
                    hbase_version = "1.2.0-${envClassifier.value}"
                    hadoop_version = "2.6.0-${envClassifier.value}"
                    kafka_version = "0.10.0-kafka-2.1.0"
                } else if ("$env_classifier" == "cdh5.16.1"){
                    spark_folder = "."
                    hbase_version = "1.2.0-${envClassifier.value}"
                    hadoop_version = "2.6.0-${envClassifier.value}"
                    kafka_version = "0.10.0-kafka-2.1.0"
                } else if ("$env_classifier" == "cdh6.3.0"){
                    spark_folder = "spark2.4"
                    hbase_version = "2.1.0-${envClassifier.value}"
                    hadoop_version = "3.0.0-${envClassifier.value}"
                    kafka_version = "2.2.1-${envClassifier.value}"
                } else if ("$env_classifier" == "hdp2.6.1"){
                    spark_folder = "."
                    hbase_version = "1.1.2.2.6.1.0-129"
                    hadoop_version = "2.7.3.2.6.1.0-129"
                    kafka_version = "0.10.1.2.6.1.0-129"
                } else if ("$env_classifier" == "hdp2.6.3"){
                    spark_folder = "."
                    hbase_version = "1.1.2.2.6.3.0-235"
                    hadoop_version = "2.7.3.2.6.3.0-235"
                    kafka_version = "0.10.1.2.6.3.0-235"
                } else if ("$env_classifier" == "hdp2.6.4"){
                    spark_folder = "."
                    hbase_version = "1.1.2.2.6.4.0-91"
                    hadoop_version = "2.7.3.2.6.4.0-91"
                    kafka_version = "0.10.1.2.6.4.0-91"
                } else if ("$env_classifier" == "hdp2.6.5"){
                    spark_folder = "spark2.3"
                    hbase_version = "1.1.2.2.6.5.0-292"
                    hadoop_version = "2.7.3.2.6.5.0-292"
                    kafka_version = "1.0.0.2.6.5.0-292"
                } else if ("$env_classifier" == "hdp3.1.0"){
                    spark_folder = "spark2.3"
                    hbase_version = "2.0.2.3.1.0.0-78"
                    hadoop_version = "3.1.1.3.1.0.61-1"
                    kafka_version = "2.0.0.3.1.0.0-78"
                }
 
                branch = source_branch[-2..-1]

                if ( "$branch" == ".8" ) {
                        sh """
                        cd $spark_folder
                        sed -i '/^spliceVersion/d' build.sbt
                        sed -i '/^envClassifier :=.*/d' build.sbt
                        sed -i '/^hbaseVersion :=.*/d' build.sbt
                        sed -i '/^hadoopVersion :=.*/d' build.sbt
                        touch temp.log
			echo 'val spliceVersion = "${splice_version}"' > temp.log
                        echo 'envClassifier := "${env_classifier}"' >> temp.log
                        echo 'hbaseVersion := s"${hbase_version}"' >> temp.log
                        echo 'hadoopVersion := s"${hadoop_version}"' >> temp.log
                        echo 'kafkaVersion := s"${kafka_version}"' >> temp.log
			head -n25 build.sbt > temp2.log
                        cat temp.log >> temp2.log
                        tail -n +26 build.sbt >> temp2.log
                        mv temp2.log build.sbt
                        cat build.sbt
                        sbt package
                        sbt assemble
                        """
                    } else if ( "$branch" == ".0" ) {
                        sh """
                        cd $spark_folder
                        sed -i '/spliceVersion =/d' build.sbt
                        sed -i '/^envClassifier :=.*/d' build.sbt
                        sed -i '/^hbaseVersion :=.*/d' build.sbt
                        sed -i '/^hadoopVersion :=.*/d' build.sbt
                        sed -i '/^kafkaVersion :=.*/d' build.sbt
                        touch temp.log
			echo 'val spliceVersion = "${splice_version}"' > temp.log
                        echo 'envClassifier := "${env_classifier}"' >> temp.log
                        echo 'hbaseVersion := s"${hbase_version}"' >> temp.log
                        echo 'hadoopVersion := s"${hadoop_version}"' >> temp.log
                        echo 'kafkaVersion := s"${kafka_version}"' >> temp.log
			head -n25 build.sbt > temp2.log
                        cat temp.log >> temp2.log
                        tail -n +26 build.sbt >> temp2.log
                        mv temp2.log build.sbt
                        cat build.sbt
                        sbt package
                        sbt assemble
                        """
                    } else {
                        sh """
                        cd $spark_folder
                        sed -i '/^/spliceVersion/d' build.sbt
                        sed -i '/^spliceVersion/d' build.sbt
                        sed -i '/^envClassifier :=.*/d' build.sbt
                        sed -i '/^hbaseVersion :=.*/d' build.sbt
                        sed -i '/^hadoopVersion :=.*/d' build.sbt
                        sed -i '/^kafkaVersion :=.*/d' build.sbt
                        touch temp.log
			echo 'val spliceVersion = "${splice_version}"' > temp.log
                        echo 'envClassifier := "${env_classifier}"' >> temp.log
                        echo 'hbaseVersion := s"${hbase_version}"' >> temp.log
                        echo 'hadoopVersion := s"${hadoop_version}"' >> temp.log
                        echo 'kafkaVersion := s"${kafka_version}"' >> temp.log
			head -n25 build.sbt > temp2.log
                        cat temp.log >> temp2.log
                        tail -n +26 build.sbt >> temp2.log
                        mv temp2.log build.sbt
                        cat build.sbt
                        sbt package
                        sbt assemble
                        """
                    }
            }
      }
    } catch (any) {
        // if there was an exception thrown, the build failed
        currentBuild.result = "FAILED"
        throw any

    } finally {
        // success or failure, always send notifications
        notifyBuild(currentBuild.result)
    }
}

def notifyBuild(String buildStatus = 'STARTED') {
    // Build status of null means successful.
    buildStatus =  buildStatus ?: 'SUCCESSFUL'
    // Override default values based on build status.
    if (buildStatus == 'STARTED' || buildStatus == 'INPUT') {
        color = 'YELLOW'
        colorCode = '#FFFF00'
    } else if (buildStatus == 'CREATING' || buildStatus == 'DESTROYING'){
        color = 'BLUE'
        colorCode = '#0000FF'
    } else if (buildStatus == 'SUCCESSFUL') {
        color = 'GREEN'
        colorCode = '#00FF00'
    } else if (buildStatus == 'FAILED'){
        color = 'RED'
        colorCode = '#FF0000'
    } else {
        echo "End of pipeline"
    }
}

