#!groovy
@Library('amf-jenkins-library') _

pipeline {
  agent {
    dockerfile true
  }
  environment {
    NEXUS = credentials('exchange-nexus')
    GITHUB_ORG = 'aml-org'
    GITHUB_REPO = 'scala-common'
  }
  stages {
    stage('Test') {
      steps {
        sh 'sbt clean coverage test coverageReport'
      }
    }
    stage('Coverage') {
      when {
        anyOf {
          branch 'master'
          branch 'sonar-onboard'
        }
      }
      steps {
        wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
          withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'sonarqube-official', passwordVariable: 'SONAR_SERVER_TOKEN', usernameVariable: 'SONAR_SERVER_URL']]) {
            script {
              sh 'sbt -Dsonar.host.url=${SONAR_SERVER_URL} sonarScan'
            }
          }
        }
      }
    }
    stage('Publish') {
      when {
        branch 'master'
      }
      steps {
        wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
          sh '''
              echo "about to publish in sbt"
              sbt commonJS/publish
              sbt commonJVM/publish
              echo "sbt publishing successful"
          '''
        }
      }
    }
    stage('Tag version') {
      when {
        branch 'master'
      }
      steps {
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'github-salt', passwordVariable: 'GITHUB_PASS', usernameVariable: 'GITHUB_USER']]) {
          script {
            def version = sbtArtifactVersion("commonJVM")
            tagCommitToGithub(version)
          }
        }
      }
    }
  }
}
