	# Release to Maven Central

1. Create a [ticket with Sonatype](http://central.sonatype.org/pages/ossrh-guide.html)  
(This has to be done by our maintenance department once per project).

2. Install a [gpg client](http://central.sonatype.org/pages/apache-maven.html#other-prerequisites) to sign the deployment artifacts  
(This step has obviously to be done once per client).

3. Once you have pushed all changes to github set the release version:  
`$ mvn versions:set -DnewVersion=<VERSION>`

4. Deploy the release to the Sonatype Staging Server:  
`$ mvn clean deploy`

5. Release the artifact from the Staging Server:  
`$ mvn nexus-staging:release`
 
6. Once the release is done, commit and tag the release:  
`$ git commit -am "Released version: <VERSION>"`  
`$ git tag <VERSION>`

7. Set new snapshot version:  
`$ mvn versions:set -DnewVersion=<NEXT-VERSION>-SNAPSHOT`

8. Commit snapshot version:  
`$ git commit -am "Updated from version: <VERSION> to: <NEXT-VERSION>-SNAPSHOT"`

9. Push all changes to Github:  
`$ git push origin master`  
`$ git push origin --tags`
 