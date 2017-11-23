all:
	javac chess/*.java
	javac *.java

clean:
	find . -name '*.class' -exec rm {} \;

redo:
	touch *.java
	make

dot:
	dot -Tpng state_diagram.dot -o state_diagram.png