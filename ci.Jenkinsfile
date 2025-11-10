@Library("ci@master") _

MavenDockerPublish(
   'enableCodeSonarSastScan': true,
   'enableCodeSonarSastGating': false,
   'tenant': 'omega',
   'jdkVersion': '17',
   'codeSonarScanCommand': "mvn sonar:sonar -Dsonar.projectKey=zea-opc-b03-digital-kyc-vanushree -Dsonar.projectName=\"Digital-kyc-verification-tracker\" -Dsonar.branch.name=${params.gitBranch ?: 'master'}",
   'mavenBuildCommand': 'mvn clean package',
   'mavenDeployCommand': 'mvn clean deploy',
   'disableImagePublish': false,
   'publishHelmChart': true,
)