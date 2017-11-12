all:
	javac *.java

clean:
	rm *.class

redo:
	touch *.java
	make