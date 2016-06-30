Guava学习笔记
===============================
github地址：https://github.com/google/guava    
官方文档：https://github.com/google/guava/wiki   
翻译：http://ifeve.com/google-guava/   

_Annotations_
------------------------
Annotation包下共有4个注解
Beta、GwtCompatible、GwtIncompatible、VisibleForTesting    
1.Beta  
在guava中标记Beta注解表示这个类、方法、属性还不稳定，
有可能在以后的版本中变化，或者去掉，所以不建议大量使用。

2.VisibleForTesting     
这是一个不推荐使用的注解，在大部分情况下单元测试只测试公共方法。
这个注解仅仅是一个标记，表示被注解的部分可以被单独测试，不能被加载到JVM中。

3.4.GwtCompatible、GwtIncompatible   
这两个注解表示是否被Google Web Toolkit (GWT)所兼容，
注解到方法时表示返回的类型是否可以被GWT所兼容。   

**总结：** 
这些注解都是用来给开发人员看的，方便开发人员开发和编写开发文档。
我们一般情况下不会使用到。

_Reflect_
-------------------
### Reflection  
- getPackageName:获取包名
- initialize：执行静态初始化,可加载多个。Class.forName()
- newProxy: 本质还是JDK动态代理，多了InvocationHandler参数检查，Class接口判断，类型转换。
```
JDK:
Foo foo = (Foo) Proxy.newProxyInstance(
Foo.class.getClassLoader(),
new Class<?>[] {Foo.class},
invocationHandler);
Guava:
Foo foo = Reflection.newProxy(Foo.class, invocationHandler);
```
### ClassPath   
Scans the source of a {@link ClassLoader} and finds all loadable classes and resources.
三个内部类ResourceInfo、ClassInfo、Scanner。

ResourceInfo：
用来存储可加载的资源文件信息，可以是class文件或者其他资源文件。  
拥有属性resourceName、ClassLoader。
- url：可以返回对应的URL对象（loader.getResource(resourceName);）
- asByteSource、asCharSource：返回对应URL的Guava IO操作对象。
- 重写了hashCode、toString、equals方法。

ClassInfo：存储可加载的class文件
继承ResourceInfo 多了属性className。
- getPackageName：得到包名。
- getSimpleName：类名。
- getName：全名。
- load：加载类，注意并不是初始化。    
这里我试了一下会执行静态代码块里的内容，不会执行构造器里的。（loader.loadClass(className);）

Scanner：扫描出resource和class的文件信息，主要供ClassPath使用。

ClassPath：
- from：Returns a {@code ClassPath} representing all classes and resources loadable from {@code
          classloader} and its parent class loaders.
       返回该classLoader的、和他父classLoader的 所有文件信息ClassPath。
- getResources:return ImmutableSet\<ResourceInfo>
- getAllClasses:ImmutableSet\<ClassInfo>
- getTopLevelClasses:ImmutableSet\<ClassInfo>,
  与All不同的是top level的classes。
  经测试top level即不包含内部类。  
  可以传入参数（packageName 包名），返回指定包下的类。  
- getTopLevelClassesRecursive：需传入参数（包名），返回指定包和子包下的类。starts with package name。

总结：ClassPath会使用Scanner扫描出你需要的文件信息ClassInfo\ResourceInfo 进行需要的操作，类初始化，文件IO操作等。

### TypeToken
Guava提供了TypeToken, 它使用了基于反射的技巧甚至让你在运行时都能够巧妙的操作和查询泛型类型。  

- 初始化TypeToken对象 三种方法 new(),of(Class),of(Type)
```
TypeToken<List<String>> typeToken = new TypeToken<List<String>>() {};
TypeToken<StringList> typeToken1 = TypeToken.of(StringList.class);
TypeToken<?> typeToken2 = TypeToken.of(StringList.class.getGenericInterfaces()[0]);
```

- getType() 	
获得包装的java.lang.reflect.Type.
- getRawType()	    
返回大家熟知的运行时类（Class）
- getTypes()	
返回一个Set，包含了这个所有接口，子类和类是这个类型的类。返回的Set同样提供了classes()和interfaces()方法允许你只浏览超类和接口类。
- getSubtype(Class<?>)	    
返回那些有特定原始类的子类型。举个例子，如果这有一个Iterable并且参数是List.class，那么返回将是List。
- getSupertype(Class<?>)	
产生这个类型的超类，这个超类是指定的原始类型。举个例子，如果这是一个Set并且参数是Iterable.class，结果将会是Iterable。
- 补充：上面这两个方法都是返回对应子类或父类的TypeToken，如果传入参数不是父类或子类将抛出IllegalArgumentException
- isSupertypeOf(typeToken)、isSupertypeOf(type)  
这个类型是否是指定类型的父类。
- isSubtypeOf(typeToken)、isSubtypeOf(type)  
这个类型是否是指定类型的子类。
- isArray()	    
检查某个类型是不是数组，甚至是<? extends A[]>。
- isPrimitive()     
检查是否是9种基本类型,包括void
- getComponentType()	 
返回组件类型数组。
- where(TypeParameter<X> typeParam,Class<X> typeArg)    
动态的解决泛型类型参数,TypeTokenTest line:1852 mapOf、arrayOf
- resolveType(Type type)    
这可以被用来一般性地推断出在一个类型相关方法的返回类型
- constructor、method
返回对应的Invokable

### AbstractInvocationHandler
有时候你可能想动态代理能够更直观的支持equals()，hashCode()和toString()，那就是：  

1.一个代理实例equal另外一个代理实例，只要他们有同样的接口类型和equal的invocation handlers。   
2.一个代理实例的toString()会被代理到invocation handler的toString()，这样更容易自定义。 
AbstractInvocationHandler实现了以上逻辑。

除此之外，AbstractInvocationHandler确保传递给handleInvocation(Object, Method, Object[]))的参数数组永远不会空，从而减少了空指针异常的机会。

### Element
代表一个Field、Method或Constructor。用于查看、判断所代表的属性。 
```
 Element element = new Element(A.class.getDeclaredField(name));
 
 Constructor<?> constructor = A.class.getDeclaredConstructor(Object.class);
 Element element = new Element(constructor);
 
 Element element = new Element(A.class.getDeclaredMethod(name, parameterTypes));
```
- getOwnerType(): 返回所有者的TypeToken
- isPublic()
- isProtected()
- isPackagePrivate()
- isPrivate()
- isStatic()
- isFinal()
- isAbstract()
- isNative()
- isSynchronized()
- isVolatile()
- isTransient()
- isAnnotationPresent(annotationClass):判断该代表的<?>是否被annotationClass注解。

### Parameter
Represents a method or constructor parameter.
- getType():返回这个参数的TypeToken
- getDeclaringInvokable():返回声明这个参数的Invokable

### TypeCapture\<T>
Captures the actual type of {@code T} 捕获T的实际Type    
非公共类，guava内部使用
- capture()：返回T的Type    

``Type type = new TypeCapture<String>() {}.capture();``

### Invokable
Guava的Invokable是对java.lang.reflect.Method和java.lang.reflect.Constructor的流式包装。它简化了常见的反射代码的使用。    
继承了Element，是一个抽象类，下面方法中返回的是Invokable的具体实现类MethodInvokable与ConstructorInvokable。

- Invokable<?, Object> from(Method method)

- Invokable<T, T> from(Constructor<T> constructor)  
返回方法或构造器的Invokable。
- isOverridable()
- isVarArgs()：是否是可变的参数数目
- getReturnType()
- getParameters()：return ImmutableList\<Parameter>
- getExceptionTypes(): return ImmutableList<TypeToken<? extends Throwable>>   InvokableTest:testConstructor_exceptionTypes
- Invokable<T, R1> returning(Class<R1> returnType)
- Invokable<T, R1> returning(TypeToken<R1> returnType)
返回明确指定R1的invokable,使用from会返回泛型为\<?,Object>，使用returning返回\<?,R1>，可指定类型。
- getOwnerType()：返回拥有该method或constructor的类的TypeToken  InvokableTest:testGetOwnerType_constructor
- invoke(@Nullable T receiver, Object... args)
执行方法，receiver传空时则执行被包装的方法/构造方法。传入对象时则执行该对象的方法/构造器。
```
    Invokable<?, Iterable> delegate = Prepender.method(
            "prepend", String.class, Iterable.class)
        .returning(Iterable.class);
    Iterable<String> result = delegate.invoke(null, "a", ImmutableList.of("b", "c"));
    assertEquals(ImmutableList.of("a", "b", "c"), ImmutableList.copyOf(result));
    
    Invokable<Prepender, Iterable<String>> delegate = Prepender.method(
                "prepend", Iterable.class)
            .returning(new TypeToken<Iterable<String>>() {});
    Iterable<String> result = delegate.invoke(new Prepender("a", 2), ImmutableList.of("b", "c"));
    assertEquals(ImmutableList.of("a", "a", "b", "c"), ImmutableList.copyOf(result));
```

### TypeVisitor
用来检查Type，对不同类型的Type进行处理。
<pre>   {@code
 *   new TypeVisitor() {
 *     protected void visitParameterizedType(ParameterizedType t) {
 *       visit(t.getOwnerType());
 *       visit(t.getActualTypeArguments());
 *     }
 *     protected void visitGenericArrayType(GenericArrayType t) {
 *       visit(t.getGenericComponentType());
 *     }
 *     protected void visitTypeVariable(TypeVariable<?> t) {
 *       throw new IllegalArgumentException("Cannot contain type variable.");
 *     }
 *     protected void visitWildcardType(WildcardType t) {
 *       throw new IllegalArgumentException("Cannot contain wildcard type.");
 *     }
 *   }.visit(type);}</pre>
 
 GenericArrayType 表示一种数组类型，其组件类型为参数化类型或类型变量。    
 ParameterizedType 表示参数化类型，如 Collection<String>。    
 TypeVariable 是各种类型变量的公共高级接口。   
 WildcardType 表示一个通配符类型表达式，如 ?、? extends Number 或 ? super Integer。

### TypeResolver
- resolveType()
上面的TypeVisitor是检查具体的类型做不同处理，TypeResolver是将Type转换为具体的类型。
```
public Type resolveType(Type type) {
    checkNotNull(type);
    if (type instanceof TypeVariable) {
      return typeTable.resolve((TypeVariable<?>) type);
    } else if (type instanceof ParameterizedType) {
      return resolveParameterizedType((ParameterizedType) type);
    } else if (type instanceof GenericArrayType) {
      return resolveGenericArrayType((GenericArrayType) type);
    } else if (type instanceof WildcardType) {
      return resolveWildcardType((WildcardType) type);
    } else {
      // if Class<?>, no resolution needed, we are done.
      return type;
    }
  }
```

- where() 返回指定泛型的TypeResolver   
``new TypeResolver().where(new TypeCapture<T[]>() {}.capture(), int[].class)``

### Types
非公共方法，内部Type工具。     
直接创建各类型，获取类型。     
newArrayType()、
、newParameterizedType()
、newArtificialTypeVariable()
、getComponentType()
等。

### ImmutableTypeToInstanceMap、MutableTypeToInstanceMap
这两个类都是用来存储Type的Map，实现TypeToInstanceMap接口，相当于Type当key，具体值或对象为value。    
不同点在于ImmutableTypeToInstanceMap是不可变的，需要在创建该map时就初始化。    
而MutableTypeToInstanceMap是可变的，可以进行put操作。    
```
//ImmutableTypeToInstanceMap
ImmutableTypeToInstanceMap<Number> map = ImmutableTypeToInstanceMap.<Number>builder()
        .put(Integer.class, 0)
        .put(int.class, 1)
        .build();
        
//MutableTypeToInstanceMap
MutableTypeToInstanceMap map = new MutableTypeToInstanceMap<Object>();
map.putInstance(Integer.class, new Integer(5));
```

