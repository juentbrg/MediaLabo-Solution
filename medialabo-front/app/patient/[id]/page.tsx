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

interface AssessmentResponse {
    patientId: string
    age: number
    triggerCount: number
    risk: string
}

const PatientDetailPage: FC = () => {
    const { id } = useParams() as { id: string }
    const router = useRouter()

    const [patient, setPatient] = useState<PatientType | null>(null)
    const [notes, setNotes] = useState<Note[]>([])
    const [loading, setLoading] = useState<boolean>(true)
    const [riskResult, setRiskResult] = useState<AssessmentResponse | null>(null)

    const [isAddOpen, setIsAddOpen] = useState<boolean>(false)
    const emptyNote: NoteForm = { patId: id, note: '' }
    const [newNote, setNewNote] = useState<NoteForm>(emptyNote)

    const [isEditOpen, setIsEditOpen] = useState<boolean>(false)
    const [currentEdit, setCurrentEdit] = useState<EditNoteForm | null>(null)

    useEffect(() => {
        if (!id) return

        const fetchData = async (): Promise<void> => {
            try {
                const [pRes, nRes] = await Promise.all([
                    axios.get<PatientType>(`${process.env.NEXT_PUBLIC_API_URL}/api/patient/${id}`, {
                        withCredentials: true,
                    }),
                    axios.get<Note[]>(`${process.env.NEXT_PUBLIC_API_URL}/api/note/${id}`, {
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

    const handleAddChange = (e: ChangeEvent<HTMLTextAreaElement>): void => {
        setNewNote(prev => ({ ...prev, note: e.target.value }))
    }

    const handleAddSubmit = async (form: NoteForm): Promise<void> => {
        await axios.post(`${process.env.NEXT_PUBLIC_API_URL}/api/note/insert`, form, {
            withCredentials: true,
        })
        setIsAddOpen(false)
        setNewNote(emptyNote)
        const nRes = await axios.get<Note[]>(`${process.env.NEXT_PUBLIC_API_URL}/api/note/${id}`, {
            withCredentials: true,
        })
        setNotes(nRes.data)
    }

    const handleDeleteNote = async (noteId: string): Promise<void> => {
        await axios.delete(`${process.env.NEXT_PUBLIC_API_URL}/api/note/delete/${noteId}`, {
            withCredentials: true,
        })
        setNotes(prev => prev.filter(n => n.id !== noteId))
    }

    const handleEditClick = (note: Note): void => {
        setCurrentEdit({ ...note })
        setIsEditOpen(true)
    }

    const handleEditChange = (e: ChangeEvent<HTMLTextAreaElement>): void => {
        if (currentEdit) {
            setCurrentEdit({ ...currentEdit, note: e.target.value })
        }
    }

    const handleEditSubmit = async (form: EditNoteForm): Promise<void> => {
        await axios.put(`${process.env.NEXT_PUBLIC_API_URL}/api/note/update/${form.id}`, form, {
            withCredentials: true,
        })
        setIsEditOpen(false)
        setCurrentEdit(null)
        setNotes(prev =>
            prev.map(n => (n.id === form.id ? { ...n, note: form.note } : n))
        )
    }

    const handleRiskEvaluation = async (): Promise<void> => {
        try {
            const res = await axios.get<AssessmentResponse>(
                `${process.env.NEXT_PUBLIC_API_URL}/api/assess/${id}`,
                { withCredentials: true }
            )

            const updatedAssessment = { ...res.data, patientId: id }
            setRiskResult(updatedAssessment)
        } catch (error) {
            console.error("Erreur lors de l'évaluation du risque :", error)
        }
    }

    const handleAddClose = (): void => {
        setIsAddOpen(false)
        setNewNote(emptyNote)
    }

    const handleEditClose = (): void => {
        setIsEditOpen(false)
        setCurrentEdit(null)
    }

    if (loading) {
        return <p className="p-6 text-center">Chargement…</p>
    }

    if (!patient) {
        return <p className="p-6 text-center text-red-600">Patient introuvable.</p>
    }

    return (
        <main className="min-h-screen bg-gray-50 p-10">
            <button
                onClick={() => router.push('/')}
                className="mb-6 inline-flex items-center px-4 py-2 bg-white text-gray-700 border border-gray-300 rounded-lg shadow-sm cursor-pointer hover:bg-gray-100 transition"
            >
                <Image src={arrowLeft} alt="Retour à la liste" width={20} height={20} className="mr-2" />
                Retour à la liste
            </button>

            <h1 className="text-3xl font-bold mb-6">
                Détail de {patient.firstName} {patient.lastName}
            </h1>

            <div className="w-full flex justify-between">
                <button
                    onClick={() => setIsAddOpen(true)}
                    className="mb-6 px-4 py-2 bg-blue-500 text-white rounded-lg shadow cursor-pointer hover:bg-blue-600 transition"
                >
                    Ajouter une note
                </button>
                <button
                    onClick={handleRiskEvaluation}
                    className="mb-6 px-4 py-2 bg-blue-500 text-white rounded-lg shadow cursor-pointer hover:bg-blue-600 transition"
                >
                    Évaluer le risque de diabète
                </button>
            </div>

            <section className="bg-white rounded-lg shadow p-6 mb-4">
                <h2 className="text-2xl font-semibold mb-4">Infos patient</h2>
                <ul className="space-y-2 text-gray-800">
                    <li>🎂 Date de naissance : {patient.birthDate}</li>
                    <li>🏠 Adresse : {patient.address ?? '—'}</li>
                    <li>📞 Téléphone : {patient.phone ?? '—'}</li>
                    <li>⚧ Genre : {patient.gender === 'MALE' ? 'Homme' : 'Femme'}</li>
                </ul>
            </section>

            {riskResult && (
                <section className="mt-4 mb-4 p-4 bg-yellow-50 border-l-4 border-yellow-400 text-yellow-800 rounded">
                    <h2 className="text-2xl font-semibold mb-4">Évaluation de {patient.firstName}</h2>
                    <ul className="mt-2 space-y-1">
                        <li>👤 Âge : {riskResult.age}</li>
                        <li>⚠️ Risque : <strong>{riskResult.risk}</strong></li>
                        <li>🧬 Déclencheurs détectés : {riskResult.triggerCount}</li>
                    </ul>
                </section>
            )}

            <section className="bg-white rounded-lg shadow p-6">
                <h2 className="text-2xl font-semibold mb-4">Notes associées</h2>
                {notes.length > 0 ? (
                    <ul className="space-y-4">
                        {notes.map(n => (
                            <li key={n.id} className="flex justify-between items-start border-b pb-2">
                                <p className="italic text-gray-700 flex-1">{n.note}</p>
                                <div className="flex items-center space-x-2 ml-4">
                                    <button
                                        onClick={() => handleEditClick(n)}
                                        className="p-1 bg-blue-500 text-white rounded cursor-pointer hover:bg-blue-600 transition"
                                        aria-label="Modifier note"
                                    >
                                        <Image src={editIcon} alt="" width={16} height={16} />
                                    </button>
                                    <button
                                        onClick={() => handleDeleteNote(n.id)}
                                        className="p-1 bg-red-500 text-white rounded cursor-pointer hover:bg-red-600 transition"
                                        aria-label="Supprimer note"
                                    >
                                        <Image src={trashIcon} alt="" width={16} height={16} />
                                    </button>
                                </div>
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p className="text-gray-500">Aucune note pour ce patient.</p>
                )}
            </section>

            <AddNoteModal
                isOpen={isAddOpen}
                onClose={handleAddClose}
                onSubmit={handleAddSubmit}
                formData={newNote}
                onChange={handleAddChange}
            />

            {currentEdit && (
                <EditNoteModal
                    isOpen={isEditOpen}
                    onClose={handleEditClose}
                    onSubmit={handleEditSubmit}
                    formData={currentEdit}
                    onChange={handleEditChange}
                />
            )}
        </main>
    )
}

export default PatientDetailPage
