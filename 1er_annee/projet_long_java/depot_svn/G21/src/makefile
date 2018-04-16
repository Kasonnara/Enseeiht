"JFLAGS = -g
JC = javac
.SUFFIXES: .java .class

compile:
	mkdir -p bin
	#$(JC) $(JFLAGS) *.java
	javac -d bin Affichage_src/*.java Evenements_src/*.java Exceptions_src/*.java main_src/*.java Affichage_src/menu_src/*.java UtilitaireDeFichiers_src/*.java main_src/joueur/*.java
	echo "\n\n Pour lancer le jeu:\n -Aller dans le dossier 'bin'\n -Entrer : 'java main_src.Main ../../ressources/'"

jar:
	javac Affichage_src/*.java Evenements_src/*.java Exceptions_src/*.java main_src/*.java Affichage_src/menu_src/*.java UtilitaireDeFichiers_src/*.java main_src/joueur/*.java


#default:
#	mkdir -p bin
#	#$(JC) $(JFLAGS) *.java
#	javac -d bin *.java
#	jar cvf Simulator.jar -C bin .
#	echo "\n\n Pour lancer le jeu, aller dans le dossier 'bin' et entrer: 'java Simulator.jar ../ressources/'"


clean:
	$(RM) bin/*.class

cleanjar:
		$(RM) *.class
