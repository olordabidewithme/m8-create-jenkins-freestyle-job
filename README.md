**Demo**

**Project:**  
CI Pipeline with Jenkinsfile (Freestyle, Pipeline, Multibranch)

**Technologies used:**  
Jenkins, Docker, Linux, Git, Java, Maven

**Project Description:**  
CI Pipeline for a Java Maven application to build and push to the repository

1. Install Build Tools (Maven, Node) in Jenkins
2. Make Docker available on Jenkins server
3. Create Jenkins credentials for a Git repository
4. Create different Jenkins job types (Freestyle, Pipeline, Multibranch pipeline) for the Java Maven project with Jenkinsfile to:
   - a. Connect to the application’s Git repository
   - b. Build Jar
   - c. Build Docker Image
   - d. Push to private DockerHub repository


1. Install Build Tools (Maven, Node) & Stage View Pulgins in Jenkins
	- Maven
		- Usage
			- For Java App, Run Tests, Build Jar File
		- Install via Pulgins (Way 1)
			- Manage Jenkins > Tools > Add Maven
				- Name : maven-3.9.2
				- Version : 3.9.2
	- Node
		- Usage
			- For Node App, Run Tests, Package & Push to repository
		- Install On Container (Way 2)
			- npm & node
			- docker exec -u 0 -it jenkins_container_id bash
			- apt update
			- Download & Install curl, curl is a command-line tool for transferring data with URLs
				- apt install curl
			- Output information such as the distribution name and version of the operating system.
				- cat /etc/issue
			- Downloads the NodeSource setup script for Node.js version 20.x and saves it as nodesource_setup.sh.
				- curl -sL https://deb.nodesource.com/setup_22.x -o nodesource_setup.sh
			- This command executes the downloaded script. The script configures your system’s package manager (like apt) to use the NodeSource repository, making it easier to install and manage Node.js.
				- bash nodesource_setup.sh
			- Installs Node.js from the newly added NodeSource repository.
				- apt install nodejs
			- verify version of node and npm
				- node -v
				- npm -v
	- Stage View Plugin
		- Usage
			- shows the progress of each stage
		- Manage Jenkins > Plugins > Available pulgins
			- search stage view > install > restart jenkins
		- Restart container
		- Manage Jenkins > Pulgins > Installed plugins 
			- verify stage view is installed

2. Make Docker available on Jenkins Server
	- Stop container
		- docker stop jenkins_container_id
	- Mount the Docker socket (/var/run/docker.sock) and re-run container
```bash
docker run -p 8080:8080 -p 50000:50000 -d \
-v jenkins_home:/var/jenkins_home \ 
-v /var/run/docker.sock:/var/run/docker.sock jenkins/jenkins:lts
```
	- install docker cli in jenkins container as root user
		- docker exec -u 0 -it jenkis_container_id bash
		- curl https://get.docker.com/ > dockerinstall && chmod 777 dockerinstall && ./dockerinstall
	- gives access to the host's Docker Engine
			- in the container, add permission (read & write)
			- chmod 666 /var/run/docker.sock
			- docker commands can be executed in jenkins container now.


3. Create Jenkins credentials for a Git repository
	- Generate access token in Git
		- settings > developer settings > personal access token (classic) > generate new token (classic)
			- token name : jenkins-git-token
	- Manage Jenkins > Security > Create Credentials
		- kind : username and password
		- id : git-credentials
		- username : olordabidewithme
		- password : access_token_from_git 

4. Create a new freestyle job "java-maven-build"
	- Connet to application build repository
		- Source code management
			- select Git
			- selct credentials : git-credentials
			- branch specifier : main
			- git url : https://github.com/olordabidewithme/m8-create-jenkins-freestyle-job.git
	- Add Builds Steps
		- test : invoke top-level Maven targets
			- Maven Version : Maven-3.9
			- Goals : test
		- build package : invoke top-level Maven targets
			- Maven Version : Maven-3.9
			- Goals : package
		- build image : execute shell
			- docker build -t java-maven-app:1.0 .
	- Check Jenkins Directory
		- /var/jenkins_home/jobs/java-maven-build
		- /var/jenkins_home/workspace/java-maven-build
			- repository files
			- target/*.jar


			
6. Build and Push to private DockerHub repository
	- create private repository in docker hub 
		- demo-app
	- create jenkins credentials for docker hub 
		- Manage Jenkins > Security > Credentials
			- kind : username and password
			- id : docker-hub-repo

	- build docker image by Jenkins
		- Configure "java-maven-build" job
			- Build environment
				- tick secret text or file > select "username and password (seperated)"
				- specify user name variable "USERNAME"
				- specify password variable "PASSWORD"
				- choose "docker-hub-repo" credentials
			- Add build steps
				- Execute Shells
					- build & push
						- docker build -t olordabidewithme/demo-app:jma-1.0 .
						- docker login -u $USERNAME -p $PASSWORD <-- this is not good practice
						- echo $PASSWORD | docker login -u $USERNAME --password-stdin
						- docker push olordabidewithme/demo-app:jma-1.0

7. Build and Push to private Nexus repository
	- create daemon.json
		- The daemon.json file is Docker's configuration file for the Docker daemon (dockerd). It allows you to configure various aspects of Docker's behavior without using command-line flags.
		- vim /etc/docker/daemon.json
```bash
{
	"insecure-registries" : ["ip:port"]
}
```
	- restart docker
		- systemctl restart docker
		- docker start jenkins_container_id
	- run as root
		- docker exec -u 0 -it jenkins_container_id bash
		- chmod 666 /var/docker/docker.sock

	- create jenkins credentials for nexus
		- Manage Jenkins > Security > Credentials
			- kind : username and password
			- id : nexus-docker-repo
	- Add build steps
		- Execute Shells
			- docker build -t 192.168.32.32:8081/java-maven-app:1.0 .
			- docker login -u $USERNAME -p $PASSWORD
			- echo $PASSWORD | docker login -u $USERNAME --password-stdin 192.168.32.32:8081
			- docker push ip:port/java-maven-app:1.0



8. Others : Create Freestyle Job [Demo Only]
	- Name : my-job
	- Build Steps
		- Step 1: execute shell
			- npm --version
				- directly installed on server, more flexible
		- Step 2 : invoke top level Maven target
			- limited to the provided input fields
			- Maven version : maven-3.9
			- Goals : --version
	- Install Nodejs Pulgin
		- Manage Jenkins > Available Pulgins 
			- Search nodejs and install it
		- Manage Jenkins > Tools > NodeJs > Add NodesJs
			- Name : my-nodejs
			- Version : NodeJS20.2.0
			- Install automatially.
	- Configure Job "my-job"
		- Step 3 : Execute NodeJS Script [Demo Only, Remove]
			- select "my-nodejs" installation
		- Step 1 : instead of running "npm --version", running from a shell script
			- Create a new branch "jenkins-jobs" and add "freestyle-build.sh"
			- Switch to the new branch "jenkins-jobs"
			- Build Step 1 : instead of running "npm --version", running from a shell script
			- ```bash
			  chmod +x freestyle-build.sh
			  ./freestyle-build.sh
			  ```
	- view jenkins data
		- docker exec -it container_id bash
		- ls /var/jenkins_home/
			- Credentials
			- Pulgins
			- Jobs
				- builds
				- ls /var/jenkins_home/jobs
				- ls /var/jenins_home/workspace/
					- repository
