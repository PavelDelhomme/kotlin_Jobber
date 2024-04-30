package com.delhomme.jobber.ex.dataclass

/*
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "projet_competence",
    primaryKeys = ["projetId", "competenceId"],
    foreignKeys = [
        ForeignKey(
            entity = Projet::class,
            parentColumns = ["id"],
            childColumns = ["projetId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Competence::class,
            parentColumns = ["id"],
            childColumns = ["competenceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["projetId"]),
        Index(value = ["competenceId"])
    ]
)
data class ProjetCompetence(
    val projetId: Int,
    val competenceId: Int
)

*/