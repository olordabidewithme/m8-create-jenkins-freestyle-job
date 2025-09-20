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

## Study Notes
	- 2 Roles
		- Administrator
			- manage jenkins, setup jenkins cluster, install plugins, backup data
		- Jenkin user
			- create actual job

	- Build tools
		- Maven
			- Java App
			- Run tests
			- build jar file
		- Npm
			- Node App
			- Run tests,
			- package and push to repostory

		- 2 ways to install
			- jenkins plugins
				- Tools
					- Maven
						-3.9.2
						-install from apache
					- Npm
						- docker exec -u 0 -it container_id bash
						- cat /etc/issue
						- apt update
						- apt install curl
						- install npm
							- curl -sL https://deb.nodesources.com/setup_20.x -o nodesource_setup.sh
							- bash nodesource_setup.sh
						- install node
							- apt install nodejs
						- verify version of npm and node
							- node -v
							- npm -v
					- stage view plugin
						- show progress of each stage
			- install directly on server
				- more flexible
			- inside jenkins container
		- create job
			- job type
				- freestyle
				- pipeline
				- multibranch pipeline
			- freestyle
				- name : my-job
				- build step 1 : execute shell
					- npm --version
				- build step 2 : invoke top-level Maven targets
					- Maven Version : maven 3.9
					- Goals : --version 
				- install nodejs plugin
					- Manage Jenkins > Pulgins > node
					- Manage Jenkins > Tools > Add NodeJS
						- Name : my-nodejs
						- Version : NodeJS 20.2.0
					- go to job
						- execute nodejs plugin
				- go to job : "my-job"
					- Add build steps
						- Execute NodeJS script
							- choose my-nodejs
							- Script: 
		- configure git repository
			- source code management
				- repository url
				- add credential
					- kind : Username with password
					- id : gitlab-credentials
					- supply username and password
		- view jenkins data
			- docker exec -it container_id bash
			- ls /var/jenkins_home/
				- Jobs
					- ls /var/jenkins_home/jobs
					- ls /var/jenins_home/workspace/
		- run shell
			- chmod +x freestyle-build.sh
			- ./freestyle-build.sh
		- run test and build java application
			- create a new job
				- java-maven-build
					- git repository url
					- select bracnh
					- build step1
						- Maven version : maven 3.9
						- Goals : test
					- build step2
						- Maven version : maven 3.9
						- Goals : package
		- make docker available inside container
					- stop container
						- docker stop jenkis_container_id
					- run new container
```bash
docker run -p 8080:8080 -p 50000:50000 -d \
-v jenkins_home:/var/jenkins_home
-v /var/run/docker.sock:/var/run/docker.sock jenkins/jenkins:lts
``` 
					- docker exec -u 0 -it jenkis_container_id bash
					- curl https://get.docker.com/ > dockerinstall && chmod 777 dockerinstall && ./dockerinstall
					-add permission (read & write)
						- chmod 666 /var/run/docker.sock
					-build step3
						- Execute shell
							- docker build -t java-maven-app:1.0
					push to docker repository (private)
						-docker hub , create private repository
							- Add credentials
			-	
					- Id: docker-hub-repo
					- tick use secret text(s) or file(s)
						- Username and password
							- USERNAME
							- PASSWORD
```bash
docker build -t olordabidewithme/demo-app:jma-1.0
docker login -u $USERNAME -p $PASSWORD
echo $PASSWORD | docker login -u $USERNAME --password-stdin
docker push olordabidewithme/demo-app:jma-1.0
```
					push to nexus
						- vim /etc/docker/daemon.json
						- insecure-registries : ["ip:8083"]
						- systemctl restart docker
						-docker start container_id
						-docker exec -u 0 it container_id bash
						-chmod 666 /var/run/docker.sock
					create creditial
						- ID: nexus-docker-repo
					change script
```bash
docker build -t ip:8083/java-maven-app:1.1
docker login -u $USERNAME -p $PASSWORD
echo $PASSWORD | docker login -u $USERNAME --password-stdin
docker push ip:8083/java-maven-app:1.1
```

