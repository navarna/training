docker run -d --name jenkins -v /var/run/docker.sock:/var/run/docker.sock -v $(which docker):/usr/bin/docker -p 8080:8080 jenkins_docker:test
echo "Attente 20 secondes"
/bin/sleep 20
docker exec jenkins /bin/sh -c "cd var/jenkins_home/secrets/;cat initialAdminPassword;exit"
docker ps
