deploy:
	mvn deploy

doc:
	rm -rf target/site/apidocs
	mvn javadoc:javadoc
	xdg-open target/site/apidocs/index.html

install:
	mvn install

package:
	mvn package

.PHONY: deploy doc install package
