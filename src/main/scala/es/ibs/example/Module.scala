package es.ibs.example

import scala.concurrent.ExecutionContext
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.google.inject.{AbstractModule, Guice, Provides, Stage}
import javax.inject.Singleton
import net.codingwell.scalaguice.ScalaModule
import es.ibs.util.di.AkkaGuiceSupport

class Module extends AbstractModule with ScalaModule with AkkaGuiceSupport {

  override def configure(): Unit = {
    binder().requireExplicitBindings()
    binder().requireAtInjectOnConstructors()
    binder().requireExactBindingAnnotations()

    bind[Config].in[Singleton]

    bind[RatesService].in[Singleton]
    bind[PgService].in[Singleton]

    bind[RestService].asEagerSingleton()
    bindActor[RatesActor].asEagerSingleton()
  }

  @Provides @Singleton
  def provideActorSystem(): ActorSystem = ActorSystem.create(Config.APP_NAME.toUpperCase)

  @Provides @Singleton
  def provideEC(as: ActorSystem): ExecutionContext = as.dispatcher

  @Provides @Singleton
  def provideMaterializer(as: ActorSystem): ActorMaterializer = ActorMaterializer()(as)
}

object Module extends App {
  Guice.createInjector(Stage.DEVELOPMENT, new Module)
}
