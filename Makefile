all:
	javac chess/*.java
	javac *.java

clean:
	find . -name '*.class' -exec rm {} \;

redo:
	touch *.java
	make