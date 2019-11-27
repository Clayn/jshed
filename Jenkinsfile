node {
   def mvnHome
   stage('Preparation') { 
			checkout scm
            mvnHome = tool 'Maven'
            dir('JShed') {
                if (isUnix()) {
                sh "'${mvnHome}/bin/mvn' clean"
            } else {
                bat(/"${mvnHome}\bin\mvn" clean/)
            }
            }
            dir('JShedFX') {
                if (isUnix()) {
                sh "'${mvnHome}/bin/mvn' clean"
            } else {
                bat(/"${mvnHome}\bin\mvn" clean/)
            }
            }
        }
   dir('JShed') {
        
		stage('Build') {
            if (isUnix()) {
                sh "'${mvnHome}/bin/mvn' -DskipTests install"
            } else {
                bat(/"${mvnHome}\bin\mvn" -DskipTests install/)
            }
        }
        stage('Test') {
            if (isUnix()) {
                sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore=true test"
            } else {
                bat(/"${mvnHome}\bin\mvn" -Dmaven.test.failure.ignore=true test/)
            }
        }
        stage('Results') {
            junit allowEmptyResults: true, testResults: '**/TEST-*.xml'
        }
   }
	dir('JShedFX') {
        
		stage('Build') {
            if (isUnix()) {
                sh "'${mvnHome}/bin/mvn' -DskipTests install"
            } else {
                bat(/"${mvnHome}\bin\mvn" -DskipTests install/)
            }
        }
        stage('Test') {
            if (isUnix()) {
                sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore=true test"
            } else {
                bat(/"${mvnHome}\bin\mvn" -Dmaven.test.failure.ignore=true test/)
            }
        }
        stage('Results') {
            junit allowEmptyResults: true, testResults: '**/TEST-*.xml'
        }
   }
}
