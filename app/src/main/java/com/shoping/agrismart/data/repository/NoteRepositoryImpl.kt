package com.shoping.agrismart.data.repository

import com.shoping.agrismart.data.local.dao.NoteDao
import com.shoping.agrismart.data.local.entity.NoteEntity
import com.shoping.agrismart.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {
    override fun getAllNotes(): Flow<List<NoteEntity>> = noteDao.getAllNotes()
    override suspend fun insertNote(note: NoteEntity) = noteDao.insertNote(note)
    override suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)
}
