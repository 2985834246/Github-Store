package zed.rainxch.githubstore.feature.search.domain.model;

enum class ProgrammingLanguage(val displayName: String, val queryValue: String?) {
    All("All Languages", null),
    Kotlin("Kotlin", "kotlin"),
    Java("Java", "java"),
    JavaScript("JavaScript", "javascript"),
    TypeScript("TypeScript", "typescript"),
    Python("Python", "python"),
    Swift("Swift", "swift"),
    Rust("Rust", "rust"),
    Go("Go", "go"),
    CSharp("C#", "c#"),
    CPlusPlus("C++", "c++"),
    C("C", "c"),
    Dart("Dart", "dart"),
    Ruby("Ruby", "ruby"),
    PHP("PHP", "php");

    companion object {
        fun fromLanguageString(lang: String?): ProgrammingLanguage {
            if (lang == null) return All
            return entries.find {
                it.queryValue?.equals(lang, ignoreCase = true) == true
            } ?: All
        }
    }
}