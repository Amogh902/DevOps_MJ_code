#  DevOps-MJ Project

*End-to-End CI/CD Pipeline with AWS, Terraform, Ansible, Jenkins & Tomcat*

![AWS](https://img.shields.io/badge/AWS-Cloud-orange?logo=amazonaws)
![Terraform](https://img.shields.io/badge/Terraform-IaC-purple?logo=terraform)
![Ansible](https://img.shields.io/badge/Ansible-Automation-red?logo=ansible)
![Jenkins](https://img.shields.io/badge/Jenkins-CI%2FCD-blue?logo=jenkins)
![Tomcat](https://img.shields.io/badge/Tomcat-App%20Server-yellow?logo=apachetomcat)
![Status](https://img.shields.io/badge/Status-Completed-brightgreen)

---

## Overview

This project demonstrates a **fully automated DevOps pipeline** that provisions AWS infrastructure, configures servers, and deploys a Java web application using **Terraform, Ansible, and Jenkins**.

**Live Workflow:**

1. **Terraform** provisions AWS infra
2. **Ansible** configures servers (Nginx, Tomcat, MySQL connector)
3. **Jenkins** builds & deploys WAR file to Tomcat
4. **Nginx** reverse proxy serves app publicly
5. **GitHub Webhooks** trigger Jenkins builds automatically on new commits

---

## Architecture

![Architecture Diagram](images/architecture.png)

 Architecture Details

* **Public Subnet** → Jenkins, Ansible, Proxy (Nginx)
* **Private Subnet** → Tomcat App Server
* **Database** → RDS MySQL (private subnet only)
* **Proxy Layer** → Nginx forwards HTTP → Tomcat

![](/DevOps_MJ_img/VPC-detail-resources.png)


---

## Infrastructure (Terraform)

Provisioned Resources

* **Networking**

  * VPC → `10.0.0.0/16`
  * Subnets → Public (`10.0.32.0/20`), Private (`10.0.0.0/20`, `10.0.16.0/20`)
  * IGW, NAT Gateway

* **Security**

  * App SG → SSH, HTTP, Tomcat (8080)
  * DB SG → MySQL only from App SG

* **Compute**

  * Jenkins Server (Ubuntu)
  * Ansible Server (Ubuntu)
  * Proxy Server (Amazon Linux)
  * App Server (Amazon Linux, private)

* **Database**

  * RDS MySQL (username: `admin`, password: `admin12345`)

📸 *Terraform Outputs:*
![](/DevOps_MJ_img/Terraform-output.png)



---

## Configuration (Ansible)

Playbooks

* **Proxy Server**

  * Installs Nginx
  * Configures reverse proxy → private Tomcat

* **App Server**

  * Installs Java 17 & Tomcat 10
  * Configures MySQL connector
  * Manages Tomcat service

📸 *Ansible Run Example:*
![](/DevOps_MJ_img/Ansible-output.png)


---

## CI/CD Pipeline (Jenkins)


Pipeline Workflow

1. **Checkout** → Pulls code from GitHub
2. **Build** → Maven `clean package` creates WAR
3. **Deploy** → WAR copied to Tomcat via SSH
4. **Restart** → Tomcat service restarted

*Key Jenkinsfile Snippet:*

```groovy
stage('Deploy to Tomcat') {
    steps {
        sshagent([SSH_CRED_ID]) {
            sh """
                WAR_FILE=\$(ls target/*.war | head -n 1)
                scp -o StrictHostKeyChecking=no \$WAR_FILE ec2-user@${SERVER_IP}:/tmp/
                ssh -o StrictHostKeyChecking=no ec2-user@${SERVER_IP} '
                    sudo rm -rf ${TOMCAT_PATH}/*
                    sudo mv /tmp/*.war ${TOMCAT_PATH}/ROOT.war
                    sudo chown tomcat:tomcat ${TOMCAT_PATH}/ROOT.war
                    sudo systemctl restart ${TOMCAT_SVC}
                '
            """
        }
    }
}
```



---

## Deployment

Once Jenkins pipeline completes → App is accessible via **proxy public IP**:

 `http://<proxy-server-public-ip>/`

*Nginx Proxy Config:*

```nginx
server {
    listen 80;
    location / {
        proxy_pass http://10.0.11.178:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

*App Homepage:*
![](/DevOps_MJ_img/output-1.png)

![](/DevOps_MJ_img/output-2.png)

![](/DevOps_MJ_img/output-3.png)

---

## Automating Builds with GitHub Webhooks

To trigger the Jenkins build automatically on code push, ensure the **GitHub Plugin** is installed on Jenkins.

**Step 1 — Enable GitHub Hook Trigger in Jenkins**

* Open your Jenkins job → Configure
* Under **Build Triggers**, check:

  * `GitHub hook trigger for GITScm polling`

![](/DevOps_MJ_img/webhook-2.png)


**Step 2 — Add Webhook in GitHub**

* Go to your repository → **Settings** → **Webhooks** → **Add webhook**
* Payload URL:

```
http://<JENKINS_SERVER_IP>:8080/github-webhook/
```

![](/DevOps_MJ_img/webhook-1.png)

Now, whenever you push code to the repo, Jenkins will automatically pull changes and deploy them.

---

##  Conclusion

This project showcases **end-to-end DevOps automation** with:

*  **Terraform** → AWS provisioning
*  **Ansible** → Configuration management
*  **Jenkins** → Continuous delivery pipeline
*  **AWS Security** → Private app, public proxy
*  **Nginx** → Public access via reverse proxy
*  **Webhooks** → Automatic builds on GitHub push

A complete **DevOps blueprint** for deploying Java applications in the cloud.

---

##  How to Use

1. Clone repo → `git clone https://github.com/Amogh902/DevOps_MJ_code.git`
2. Run Terraform → `terraform apply`
3. Run Ansible → `ansible-playbook site.yml`
4. Configure Jenkins pipeline → use provided `Jenkinsfile`
5. Enable GitHub Webhooks → auto-trigger deployments
6. Access app via → `http://<proxy-server-public-ip>/`

---

