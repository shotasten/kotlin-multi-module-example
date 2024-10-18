## MyBatisの実装方針
### 前提
例として以下のtodoテーブルを扱う際のMyBatis関連の実装方針について記載する
``` sql
CREATE TABLE todo
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(30) NOT NULL,
    description TEXT,
    status      TINYINT(1) DEFAULT 1,
    category    VARCHAR(30) NOT NULL,
    is_deleted  TINYINT(1) DEFAULT 0,
    created_at  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
```

### Mapperクラス

- interfaceを定義、命名は TableNameMapper
- @Mapperを付与
- SQLはmapperクラス内に記載する

### Entityクラス
#### 実装方針
- 命名： `TableName+Record`
- プロパティはimmutableで定義
- デフォルト値は定義しない
- テーブルの１レコード＝Entityクラス
- `@NoArg`を付与する

命名は`TableName+Record`でも`TableName+Entity`でもよいのだが、テーブルの状態を持つためのエンティティクラスだということが明確に区別できるように命名規則を設ける。１レコードと同等なのがわかりやすいように`Record`としている。
varにしてしまうと、取得後に変更ができてしまい`テーブルの１レコード＝Entityクラス`が担保できなくなる。  
デフォルト値を定義してしまうと、値を取得しなかった場合に、デフォルト値ができようされてしまい、`テーブルの１レコード＝Entityクラス`が担保できなくなる。


#### 実装例
###### Bad
``` kotlin
// NoArgを付与する
data class Todo( // 命名不適切：Entityクラスであることが明確でない
    var id: Int, // mutableで定義しない
    var title: String,
    var description: String? = null, // デフォルト値は利用しない
    var status: TodoStatus,
    var category: TodoCategory,
    var isDeleted: Boolean = false,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)
```


###### Good
``` kotlin
@NoArg
data class TodoRecord(
    val id: Int,
    val title: String,
    val description: String?,
    val status: TodoStatus,
    val category: TodoCategory,
    val isDeleted: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
```

#### Select
- `select *`で全項目取得を行い、Entityクラス（`TableNameRecord`）にマッピングする
- １レコードのすべての項目を取得しない場合、単項目で取得するか独自のDTOを定義して取得する
- 複数レコード取得する場合は`List<TableNameRecord>`で取得。取得できない場合は`emptyList`を得る
- 単レコード取得する場合は`TableNameRecord`で取得。取得できない場合は、`null`を得る
- 原則命名は`finAaaaaByCccccAndDdddd`とし、外部から何をキーに何を取ってきているかわかるようにする

###### Bad
``` kt
    @Select(
        """
        SELECT
            *
        FROM
            todo
        ORDER BY
            id ASC
    """,
    )
    fun findAll(): List<TodoRecord>? // nullableにしない

    @Select(
        """
        SELECT
            id,
            title,
            status,
        FROM
            todo
        WHERE
            id = #{id}
    """,
    )
    fun findById(id: Int): TodoRecord? //全項目取得するのにEntityクラスを利用しない
```


###### Good
``` kt
    @Select(
        """
        SELECT
            *
        FROM
            todo
        ORDER BY
            id ASC
    """,
    )
    fun findAll(): List<TodoRecord>

    @Select(
        """
        SELECT
            id,
            title,
            status,
        FROM
            todo
        WHERE
            id = #{id}
    """,
    )
    fun findOverviewById(id: Int): TodoOverviewDto?
    // 全項目取得せずに複数項目取得する場合はDTOを作成して受け取る
```

#### Insert
- insert用のDTOを作成し、Entityクラスは利用しない
- `AUTO_INCREMENT`なidは`immutable`、`nullable`デフォルト値、`null`で定義。mapperに`@Options`で指定する
  - null不許可にして、`id=0`をデフォルト値としても良いが、値が入ったかどうかがnullかどうかで判断できるほうが良さそう
- **DTOへの不要なプロパティ定義は禁止**
- 基本的にはデフォルト値は利用せず、インスタンス生成時に値を設定する


###### Bad
``` kt
    @Insert(
        """
        INSERT INTO todo
        SET
        title = #{dto.title},
        status = #{dto.status},
        category = #{dto.category}
    """,
    )
    fun insert(
        @Param("dto") toodoRecord: TodoRecord, // Entityクラスを利用している
    ): Int
```

``` kt
data class TodoInsertDto(
    val id: Int? = null,
    val title: String,
    val description: String?, // insertで利用しないプロパティを用意しない
    val status: TodoStatus,
    val category: TodoCategory,
    val isDeleted: Boolean = false, // デフォルト値はなるべく指定しない
)
```

利用者の認識齟齬で値が設定したつもりが、insertの方で利用していなかったため、想定外のデータになる可能性が高い。  
バグを起こさないために、使う値のみ設定する。

###### Good

``` kt
data class TodoInsertDto(
    val id: Int? = null,
    val title: String,
    val status: TodoStatus,
    val category: TodoCategory,
    val isDeleted: Boolean,
)
```


`@Options(useGeneratedKeys = true, keyProperty = "id")`を指定して生成されたIdを設定している。
``` kt
    @Insert(
        """
        INSERT INTO todo
        SET
        title = #{dto.title},
        status = #{dto.status},
        category = #{dto.category}
    """,
    )
    @Options(useGeneratedKeys = true, keyProperty = "id")
    fun insert(
        @Param("dto") insertDto: TodoInsertDto,
    ): Int
```

use
``` kt
        val insertData =
            TodoInsertDto(
                title = title,
                status = status,
                category = category,
            )
        println(insertData)
        // TodoInsertDto(id=null, title=Task insert, description=Description for Task insert, status=TODO, category=WORK, isDeleted=false)


        // execute
        val result = sut.insert(insertData)

        val insertedDataId = insertData.id ?: fail("insertedDataId is null")
        println(insertData)
        // TodoInsertDto(id=4, title=Task insert, description=Description for Task insert, status=TODO, category=WORK, isDeleted=false)
```

#### Update
- 更新する値のみ受け取る
  - それぞれ受け取っても良いし、DTOを利用してもよい
  - **別の用途で作成されたDTOの流用は禁止**
- 何を更新するメソッドなのかを明確にメソッド名で表現する
- DTOで受け取る汎用的なupdateメソッドにしたい場合はMyBatisのnull評価を利用する

###### Bad
```
    @Update(
        """
        UPDATE
            todo
        SET
            status = #{insertDto.status}
        WHERE
            id = #{insertDto.id}
    """,
    )
    fun updateById( // 何を更新するか明確でない
        @Param("dto") insertDto: TodoInsertDto, // 別用途のDTOをしている
    ): Int
```

```
    @Update(
        """
        UPDATE
            todo
        SET
            status = #{dto.status} // 受け取ったプロパティのうち１つしか利用していない
        WHERE
            id = #{dto.id}
    """,
    )
    fun updateTodoById( // 何を更新するか明確でない
        @Param("dto") dto: TodoUpdateDto,
    ): Int
```

別用途のDTOを利用してしまうと、更新に必要な値以外も設定する必要がある。  
ここの例でいうと、`status`のみを更新するが、`title`なども設定するので、  
利用者側で、`status`以外の値を更新できると思ってしまうリスクがあり、バグ防止のために厳格に定める。  
また、メソッド名についても同じく、利用者側で勘違いが起きないように正しく定義されたい。

###### Good
``` kt
    @Update(
        """
        UPDATE
            todo
        SET
            status = #{status}
        WHERE
            id = #{id}
    """,
    )
    fun updateStatusById(
        id: Int,
        status: TodoStatus,
    ): Int
```


汎用的なupdateを用意する場合
``` kt
    @Update(
        """<script>
            UPDATE todo
            <set>
                <if test="dto.title != null">title = #{dto.title},</if>
                <if test="dto.description != null">description = #{dto.description},</if>
                <if test="dto.status != null">status = #{dto.status},</if>
                <if test="dto.category != null">category = #{dto.category},</if>
                <if test="dto.isDeleted != null">is_deleted = #{dto.isDeleted},</if>
            </set>
            WHERE id = #{dto.id}
        </script>""",
    )
    fun updateTodo(
        @Param("dto") dto: TodoUpdateDto,
    ): Int
```

### Enumクラスのマッピング
以下のマッピングを実現するための方針を示す。  
Kotlin --- Database  
Enum <-> String  
Enum <-> Int

#### Enum <-> String
databaseでStringで定義されている値をKotlinのEnumで扱うためには以下の手順で行う。

- `StringEnumBase`を実装したEnumを定義
- `MyBatisConfiguration`の`stringEnumMappings`に当該クラスを追加
  
`StringEnumBase`を実装したEnumを定義  
TodoCategory
``` kt
enum class TodoCategory(override val value: String) : StringEnumBase<TodoCategory> {
    WORK("work"),
    PRIVATE("private"),
    HOBBY("hobby"),
    OTHER("other"),
    ;

    companion object {
        private val entityMap = entries.associateBy(TodoCategory::value)

        fun from(value: String): TodoCategory {
            return entityMap[value] ?: throw IllegalArgumentException("Unknown value: $value")
        }
    }
}
```

`MyBatisConfiguration`の`stringEnumMappings`に当該クラスを追加  
MyBatisConfiguration
```kt 
class MyBatisConfiguration {
    @Bean
    fun configurationCustomizer(): ConfigurationCustomizer {
        // omitted
                    // StringベースのEnumとそのハンドラーのマッピング
            val stringEnumMappings =
                listOf(
                    TodoCategory::class.java, // 追加
                )
        //omitted
    }
}
```

#### Enum <-> Int
databaseでIntで定義されている値をKotlinのEnumで扱うためには以下の手順で行う。

- `IntEnumBase`を実装したEnumを定義
- `MyBatisConfiguration`の`stringEnumMappings`に当該クラスを追加
  
`IntEnumBase`を実装したEnumを定義  
TodoStatus
``` kt
enum class TodoStatus(override val value: Int, val label: String) : IntEnumBase<TodoStatus> {
    TODO(1, "todo"),
    DOING(2, "doing"),
    DONE(3, "done"),
    ;

    companion object {
        private val entityMap = entries.associateBy(TodoStatus::value)

        fun from(value: Int): TodoStatus {
            return entityMap[value] ?: throw IllegalArgumentException("Unknown value: $value")
        }
    }
}
```

`val label: String`自体の定義は不要だが、ログに出力したりするときなどに人間がわかりやすいようにラベルを付けておくと良い。  



`MyBatisConfiguration`の`stringEnumMappings`に当該クラスを追加  
MyBatisConfiguration
```kt 
class MyBatisConfiguration {
    @Bean
    fun configurationCustomizer(): ConfigurationCustomizer {
        // omitted
                    // StringベースのEnumとそのハンドラーのマッピング
            val intEnumMappings =
                listOf(
                    TodoStatus::class.java,  // 追加
                )
        //omitted
    }
}
```

#### テスト実装方針
- `@MapperTest`をテストクラスに付与する
  - `MyBatisConfiguration`の内容が適用される
  - `@MybatisTest`が適用される
  - `@TestInstance(TestInstance.Lifecycle.PER_CLASS)`が適用され`@BeforeAll`が利用できるようになる
- Mock化せずに実際にDBを利用してテストを行う
- DB操作にはDbSetupを利用し、コード上で完結させる
  - [reference](https://dbsetup.ninja-squad.com/user-guide.html)


実装例
``` kt
@MapperTest
class TodoMapperTest {
    @Autowired
    private lateinit var dataSource: DataSource

    @Autowired
    private lateinit var sut: TodoMapper

    // 用途によってはテストごとにデータの入れ替えを行っても良いが、必要がなければ、テスト前に１回行うと良い
    @BeforeAll
    fun beforeAll() {
        // テストデータのセットアップ
        dbSetup(to = dataSource) {
            truncate(TABLE_NAME)

            insertInto(TABLE_NAME) {
                mappedValues(
                    "id" to testData1.id,
                    "title" to testData1.title,
                    "description" to testData1.description,
                    "status" to testData1.status.value,
                    "category" to testData1.category.value,
                    "is_deleted" to testData1.isDeleted,
                    "created_at" to testData1.createdAt,
                    "updated_at" to testData1.updatedAt,
                )
                mappedValues(
                    "id" to testData2.id,
                    "title" to testData2.title,
                    "description" to testData2.description,
                    "status" to testData2.status.value,
                    "category" to testData2.category.value,
                    "is_deleted" to testData2.isDeleted,
                    "created_at" to testData2.createdAt,
                    "updated_at" to testData2.updatedAt,
                )
                mappedValues(
                    "id" to testData3.id,
                    "title" to testData3.title,
                    "description" to testData3.description,
                    "status" to testData3.status.value,
                    "category" to testData3.category.value,
                    "is_deleted" to testData3.isDeleted,
                    "created_at" to testData3.createdAt,
                    "updated_at" to testData3.updatedAt,
                )
            }
        }.launch()
    }

    @Test
    fun testFindAll() {
        // execute
        val actual = sut.findAll()

        // verify
        assertThat(actual).containsExactlyInAnyOrder(testData1, testData2, testData3)
    }

        companion object {
        private const val TABLE_NAME = "todo"
        private val testLocalDate = LocalDateTime.of(2024, 1, 1, 10, 0, 0)
        private val testData1 =
            TodoRecord(
                id = 1,
                title = "Task 1",
                description = "Description for Task 1",
                status = TodoStatus.TODO,
                category = TodoCategory.WORK,
                isDeleted = false,
                createdAt = testLocalDate,
                updatedAt = testLocalDate,
            )
        private val testData2 =
            TodoRecord(
                id = 2,
                title = "Task 2",
                description = "Description for Task 2",
                status = TodoStatus.DOING,
                category = TodoCategory.HOBBY,
                isDeleted = false,
                createdAt = testLocalDate,
                updatedAt = testLocalDate,
            )
        private val testData3 =
            TodoRecord(
                id = 3,
                title = "Task 3",
                description = "Description for Task 3",
                status = TodoStatus.DONE,
                category = TodoCategory.OTHER,
                isDeleted = true,
                createdAt = testLocalDate,
                updatedAt = testLocalDate,
            )
    }
```
