# DOABOOKS website

This website is similar to the OAPEN website in structure and techniques used.

There are a few differences though:

- This website is bilingual. In Thymeleaf templates captions and texts are included as variables (e.g. `th:text="${home_intro}"` or `th:text="#{choose_language}`). These variables must be set in the CMS (`messages_en.properties`, `home_intro_en` etc.). 
N.B. Language files in directory `src/main/resources/i18n` are not used.
- 'Top Subjects' on the home page shows the latest addition (1 title) for each of a list of subject names that is defined in the CMS. The subject names must exist in the DSpace environment (`url_dspace_site` in `application.properties`). Retrieving the subjects from DSpace requires as much API calls as there are subject names listed, so the list size should be kept within reasonable limits (preferably no more than 10).