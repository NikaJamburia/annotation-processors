Some annotations and annotation processors that provide dependency injection functionality and creation of lazily initialized objects.

@Service - Classes annotated with this will be saved to ServiceContext. If they have any fields with this annotation, the fields will be injected automatically through setter. Having setter for injected service is necessary
@Lazy - if the services is annotated with this, the lazy proxy of your service will be generated and saved to context. You will access the lazy object from ServiceContext, just like regular object

Everything for DI and lazy init is in 'framework' package. Everything else is just for examples

The project several other annotation processors as well. They are not related to DI, I just wrote them when I was studying about annotation processing.