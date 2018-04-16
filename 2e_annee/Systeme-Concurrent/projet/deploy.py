#! /usr/bin/python3
import sys
import os
import threading
import time

liste_machine = []
with open("machines.txt","r") as f:
    for ligne in f:
        liste_machine.append(ligne.replace("\n",""))
config_template = ""
with open("config/demons/n7clusterTemplate.properties", "r") as f2:
    for ligne in f2:
        if not (ligne == "" or "#" in ligne):  
            config_template = config_template + ligne

NameNode_machine = "truite"

username = "sdeneuvi"
hidoopHome = "/home/sdeneuvi/Documents/2A/systeme_concurent/hdfs_v1/"

# run le NameNode
rNN = 'java -classpath "./bin/production/hdfs_v1:." hdfs.NameNode config/namenodes/n7cluster.properties'

def runNameNode():
    os.system(rNN)

# run un démon
rDO = 'java -classpath "./bin/production/hdfs_v1:." ordo.DaemonImpl config/demons/n7cluster{0}.properties' 
rDH = 'java -classpath "./bin/production/hdfs_v1:." hdfs.HdfsServer config/demons/n7cluster{0}.properties'

def runDemonO(nom_machine):
    os.system(rDO.format(nom_machine))
def runDemonH(nom_machine):
    os.system(rDH.format(nom_machine))
def deployDemon(nom_machine):
    os.system("ssh {0}@{1} ./deploy.py run {1}".format(username, nom_machine))
def undeploy(nom_machine):
    pass

if not len(sys.argv) == 3:
  print("bad arguments number")
  sys.exit(1)

if sys.argv[1] == "demon":
  demon_thread = threading.Thread(target=runDemonO, args=(sys.argv[2],))
  demon_thread.setDaemon(True)
  demon_thread.start()
  runDemonH(sys.argv[2])

elif sys.argv[1] == "NameNode":
    runNameNode()
elif sys.argv[1] == "stop":
    #os.system("killall java")
    os.system("pkill -f 'java -classpath'")
    #os.system("pkill -f './deploy.py'") 
elif sys.argv[1] == "deploy":
    os.system("ssh -f {0}@{1} \"cd {2}; nohup ./deploy.py NameNode {1}\"".format(username, NameNode_machine, hidoopHome))
    time.sleep(10)
    nb_machine = (len(liste_machine) if sys.argv[2] == "all" else int(sys.argv[2]))
    for machine in liste_machine[:nb_machine]:
        # generate config
        with open("config/demons/n7cluster{0}.properties".format(machine), "w") as f3:
            f3.write(config_template.replace("{1110_+_numero_du_demon_sur_la_machine}", "1110").replace("{4242_+_numero_du_demon_sur_la_machine}", "4242").replace("{192.168.x.x}", machine).replace("magicarpe", NameNode_machine))

        print("deploying :", machine)
        os.system("ssh -f {0}@{1} \"cd {2}; nohup ./deploy.py demon {1}\"".format(username, machine, hidoopHome))

elif sys.argv[1] == "undeploy":
    
    for machine in [NameNode_machine]+liste_machine: 
        print("Undeploy :",machine)
        os.system("ssh -f {0}@{1} \"cd {2}; nohup ./deploy.py stop {1}\"".format(username, machine, hidoopHome))
#ssh-copy-id user@machine

       
