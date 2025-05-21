'use client'

import React, { FC, useEffect, useState, ChangeEvent } from 'react'
import { useParams, useRouter } from 'next/navigation'
import axios from 'axios'
import Image from 'next/image'
import arrowLeft from '@/public/images/arrow-left.svg'
import editIcon from '@/public/images/edit.svg'
import trashIcon from '@/public/images/trash.svg'
import { Patient as PatientType } from '@/components/PatientTable'
import AddNoteModal, { NoteForm } from '@/components/AddNoteModal'
import EditNoteModal, { EditNoteForm } from '@/components/EditNoteModal'

interface Note {
    id: string
    patId: string
    note: string
}

const PatientDetailPage: FC = () => {
    const { id } = useParams() as { id: string }
    const router = useRouter()

    const [patient, setPatient] = useState<PatientType | null>(null)
    const [notes, setNotes] = useState<Note[]>([])
    const [loading, setLoading] = useState<boolean>(true)

    const [isAddOpen, setIsAddOpen] = useState<boolean>(false)
    const [newNote, setNewNote] = useState<NoteForm>({
        patId: id,
        note: '',
    })

    const [isEditOpen, setIsEditOpen] = useState<boolean>(false)
    const [currentEdit, setCurrentEdit] = useState<EditNoteForm | null>(null)

    useEffect(() => {
        if (!id) return

        const fetchData = async (): Promise<void> => {
            try {
                const [pRes, nRes] = await Promise.all([
                    axios.get<PatientType>(`http://localhost:8080/api/patient/${id}`, {
                        withCredentials: true,
                    }),
                    axios.get<Note[]>(`http://localhost:8080/api/note/${id}`, {
                        withCredentials: true,
                    }),
                ])
                setPatient(pRes.data)
                setNotes(nRes.data)
            } catch (error) {
                console.error('Error when loading patient:', error)
            } finally {
                setLoading(false)
            }
        }

        fetchData()
    }, [id])

    const handleAddChange = (
        e: ChangeEvent<HTMLTextAreaElement>
    ): void => {
        setNewNote((prev) => ({ ...prev, note: e.target.value }))
    }

    const handleAddSubmit = async (form: NoteForm): Promise<void> => {
        await axios.post<Note>('http://localhost:8080/api/note/insert', form, {
            withCredentials: true,
        })
        setIsAddOpen(false)
        setNewNote({ patId: id, note: '' })
        const nRes = await axios.get<Note[]>(`http://localhost:8080/api/note/${id}`, {
            withCredentials: true,
        })
        setNotes(nRes.data)
    }

    const handleDeleteNote = async (noteId: string): Promise<void> => {
        await axios.delete(`http://localhost:8080/api/note/delete/${noteId}`, {
            withCredentials: true,
        })
        setNotes((prev) => prev.filter((n) => n.id !== noteId))
    }

    const handleEditClick = (note: Note): void => {
        setCurrentEdit({ ...note })
        setIsEditOpen(true)
    }

    const handleEditChange = (
        e: ChangeEvent<HTMLTextAreaElement>
    ): void => {
        if (currentEdit) {
            setCurrentEdit({ ...currentEdit, note: e.target.value })
        }
    }

    const handleEditSubmit = async (
        form: EditNoteForm
    ): Promise<void> => {
        await axios.put<Note>(`http://localhost:8080/api/note/update/${form.id}`, form, {
            withCredentials: true,
        })
        setIsEditOpen(false)
        setCurrentEdit(null)
        setNotes((prev) =>
            prev.map((n) =>
                n.id === form.id ? { ...n, note: form.note } : n
            )
        )
    }

    if (loading) {
        return <p className="p-6 text-center">Chargement…</p>
    }
    if (!patient) {
        return (
            <p className="p-6 text-center text-red-600">
                Patient introuvable.
            </p>
        )
    }

    return (
        <main className="min-h-screen bg-gray-50 p-10">
            <button
                onClick={() => router.push('/')}
                className="mb-6 inline-flex items-center px-4 py-2 bg-white text-gray-700 border border-gray-300 rounded-lg shadow-sm cursor-pointer hover:bg-gray-100 transition"
            >
                <Image
                    src={arrowLeft}
                    alt="Retour à la liste"
                    width={20}
                    height={20}
                    className="mr-2"
                />
                Retour à la liste
            </button>
            <h1 className="text-3xl font-bold mb-6">
                Détail de {patient.firstName} {patient.lastName}
            </h1>
            <button
                onClick={() => setIsAddOpen(true)}
                className="mb-6 px-4 py-2 bg-blue-500 text-white rounded-lg shadow cursor-pointer hover:bg-blue-600 transition"
            >
                Ajouter une note
            </button>
            <section className="bg-white rounded-lg shadow p-6 mb-8">
                <h2 className="text-2xl font-semibold mb-4">
                    Infos patient
                </h2>
                <ul className="space-y-2 text-gray-800">
                    <li>🎂 Date de naissance : {patient.birthDate}</li>
                    <li>
                        🏠 Adresse : {patient.address ?? '—'}
                    </li>
                    <li>
                        📞 Téléphone : {patient.phone ?? '—'}
                    </li>
                    <li>
                        ⚧ Genre :{' '}
                        {patient.gender === 'MALE' ? 'Homme' : 'Femme'}
                    </li>
                    <li>
                        ⚠️ Risque : {patient.risque ?? '—'}
                    </li>
                </ul>
            </section>
            <section className="bg-white rounded-lg shadow p-6">
                <h2 className="text-2xl font-semibold mb-4">
                    Notes associées
                </h2>
                {notes.length > 0 ? (
                    <ul className="space-y-4">
                        {notes.map((n) => (
                            <li
                                key={n.id}
                                className="flex justify-between items-start border-b pb-2"
                            >
                                <p className="italic text-gray-700 flex-1">
                                    {n.note}
                                </p>
                                <div className="flex items-center space-x-2 ml-4">
                                    <button
                                        onClick={() => handleEditClick(n)}
                                        className="p-1 bg-blue-500 text-white rounded cursor-pointer hover:bg-blue-600 transition"
                                        aria-label="Modifier note"
                                    >
                                        <Image
                                            src={editIcon}
                                            alt=""
                                            width={16}
                                            height={16}
                                        />
                                    </button>
                                    <button
                                        onClick={() => handleDeleteNote(n.id)}
                                        className="p-1 bg-red-500 text-white rounded cursor-pointer hover:bg-red-600 transition"
                                        aria-label="Supprimer note"
                                    >
                                        <Image
                                            src={trashIcon}
                                            alt=""
                                            width={16}
                                            height={16}
                                        />
                                    </button>
                                </div>
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p className="text-gray-500">
                        Aucune note pour ce patient.
                    </p>
                )}
            </section>
            <AddNoteModal
                isOpen={isAddOpen}
                onClose={() => setIsAddOpen(false)}
                onSubmit={handleAddSubmit}
                formData={newNote}
                onChange={handleAddChange}
            />
            {currentEdit && (
                <EditNoteModal
                    isOpen={isEditOpen}
                    onClose={() => setIsEditOpen(false)}
                    onSubmit={handleEditSubmit}
                    formData={currentEdit}
                    onChange={handleEditChange}
                />
            )}
        </main>
    )
}

export default PatientDetailPage
