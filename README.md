Some annotations and annotation processors that provide dependency injection functionality and creation of lazy services.

@Service - Classes annotated with this will be saved to ServiceContext. If they have any fields with this annotation, the fields will be injected automatically

@Lazy - if the services is annotatad with this, the lazy proxy of your service will be generated saved to context

The project several other processors as well. They are not related to DI, I just wrote them when I was learning about annotation processing.