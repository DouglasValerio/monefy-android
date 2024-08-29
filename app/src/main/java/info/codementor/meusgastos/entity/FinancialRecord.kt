package info.codementor.meusgastos.entity

import java.time.Instant


class FinancialRecord(
    var _id: Int,
    val date: Instant,
    val description: String,
    val amount: Double,
    val type: FinancialRecordType,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    fun update(
        amount: Double = this.amount,
        description: String = this.description,
        date: Instant = this.date,
        type: FinancialRecordType = this.type

    ): FinancialRecord {
        val currentDateTime = Instant.now()
        return FinancialRecord(
            this._id,
            date,
            description,
            amount,
            type,
            this.createdAt,
            currentDateTime
        )
    }

    companion object {
        fun load(
            id: Int,
            date: Instant,
            description: String,
            amount: Double,
            type: FinancialRecordType,
            createdAt: Instant,
            updatedAt: Instant
        ): FinancialRecord {
            return FinancialRecord(
                id, date, description, amount, type, createdAt, updatedAt
            )
        }

        fun createExpense(
            amount: Double,
            description: String,
            date: Instant,

            ): FinancialRecord {
            val currentDateTime = Instant.now()
            return FinancialRecord(
                0,
                date,
                description,
                amount,
                FinancialRecordType.EXPENSE,
                currentDateTime,
                currentDateTime
            )
        }

        fun createIncome(
            amount: Double,
            description: String,
            date: Instant,

            ): FinancialRecord {
            val currentDateTime = Instant.now()
            return FinancialRecord(
                0,
                date,
                description,
                amount,
                FinancialRecordType.INCOME,
                currentDateTime,
                currentDateTime
            )
        }
    }

}
