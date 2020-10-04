import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import model.PostSnippet
import model.Snippet

val snippets = mutableListOf(
    Snippet("hello"),
    Snippet("world")
)

/**
 * 初始化服务
 * 启动服务器
 */
fun main(args: Array<String>) {
    embeddedServer(Netty, 8080) {
        install(CORS) {
            method(HttpMethod.Options)
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Put)
            method(HttpMethod.Delete)
            method(HttpMethod.Patch)
            header(HttpHeaders.Authorization)
            allowCredentials = true
            anyHost()
        }
        install(ContentNegotiation) {
            jackson {
                enable(SerializationFeature.INDENT_OUTPUT) // 美化输出 JSON
            }
        }
        routing {
            route("/snippets") {
                get() {
                    call.respond(mapOf("snippets" to snippets.toList()))
                }
                post() {
                    val post = call.receive<PostSnippet>()
                    for (sni in post.snippets)
                        snippets += Snippet(sni.text)
                    call.respond(mapOf("OK" to snippets.toList()))
                }
            }
        }
    }.start(wait = true)
}