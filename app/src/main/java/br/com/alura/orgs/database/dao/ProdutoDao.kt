package br.com.alura.orgs.database.dao

import androidx.room.*
import br.com.alura.orgs.model.Produto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdutoDao {

    @Query("select * from Produto")
    fun buscaTodos(): Flow<List<Produto>>

    @Query("select * from Produto order by " +
            "CASE WHEN :opcaoCrescOuDecresc = 1 THEN nome END ASC, " +
            "CASE WHEN :opcaoCrescOuDecresc = 2 THEN nome END DESC ")
    suspend fun buscaTodosOrdenadoNomeCrescOuDecresc(opcaoCrescOuDecresc: Int): List<Produto>

    @Query("select * from Produto order by " +
            "CASE WHEN :opcaoCrescOuDecresc = 1 THEN valor END ASC, " +
            "CASE WHEN :opcaoCrescOuDecresc = 2 THEN valor END DESC ")
    suspend fun buscaTodosOrdenadoValorCrescOuDecresc(opcaoCrescOuDecresc: Int): List<Produto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvaProduto(vararg obProduto: Produto)

    @Delete
    suspend fun removeProduto(vararg obProduto: Produto)

    @Query("SELECT * FROM Produto WHERE ID = :idProduto")
    suspend fun buscaPorId(idProduto: Long): Produto?
}