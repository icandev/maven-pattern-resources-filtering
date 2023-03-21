# Maven Pattern Resources Filtering

A Maven resources filtering plugin that allows to specify default value for missing filter value.

When the plugin is enabled, any missing filter value will be logged and transformed according to the `resource.filtering.default` configuration.

By default, any missing values will be written as `${key}`.

## Configuration

### Enable the plugin
```xml
<build>
   <plugins>
      <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-resources-plugin</artifactId>
          <dependencies>
              <dependency>
                  <groupId>be.icandev.maven</groupId>
                  <artifactId>maven-pattern-resources-filtering</artifactId>
                  <version>1.0.0</version>
              </dependency>
          </dependencies>
      </plugin>
   </plugins>
</build>
```

### Configure the default pattern

The following configuration will output missing values as `{{ key }}`.
```xml
<properties>
    <resource.filtering.default>{{ $key }}</resource.filtering.default>
</properties>
```

You can also use the following configuration to output empty values :
```xml
<properties>
    <resource.filtering.default />
</properties>
```


Happy coding !
