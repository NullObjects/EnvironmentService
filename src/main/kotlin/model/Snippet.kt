package model

data class Snippet(val text: String)

data class PostSnippet(val snippets: List<Snippet>)