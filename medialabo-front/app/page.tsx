'use client'

import {useEffect, useState, ChangeEvent, JSX} from 'react'
import axios from 'axios'
import AuthGuard from '@/components/AuthGuard'
import AddPatientModal from '@/components/AddPatientModal'
import EditPatientModal from '@/components/EditPatientModal'
import PatientTable from '@/components/PatientTable'
import {useRouter} from "next/navigation"

export interface Patient {
  id?: string
  firstName: string
  lastName: string
  birthDate: string
  address?: string
  phone?: string
  gender: 'MALE' | 'FEMALE'
  risque?: string
}

const emptyPatient: Patient = {
  firstName: '',
  lastName: '',
  birthDate: '',
  address: '',
  phone: '',
  gender: 'MALE',
}

export default function HomePage(): JSX.Element {
  const router = useRouter()
  const [patients, setPatients] = useState<Patient[]>([])
  const [loading, setLoading] = useState<boolean>(true)
  const [shouldRefresh, setShouldRefresh] = useState<boolean>(false)

  const [isAddOpen, setIsAddOpen] = useState<boolean>(false)
  const [addForm, setAddForm] = useState<Patient>(emptyPatient)

  const [isEditOpen, setIsEditOpen] = useState<boolean>(false)
  const [editForm, setEditForm] = useState<Patient>({ id: undefined, ...emptyPatient })

  const fetchPatients = async (): Promise<void> => {
    setLoading(true)
    try {
      const res = await axios.get<Patient[]>(`${process.env.NEXT_PUBLIC_API_URL}/api/patient`, {
        withCredentials: true,
      })
      setPatients(Array.isArray(res.data) ? res.data : [])
    } catch (err) {
      console.error('Error on fetching patients', err)
      setPatients([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchPatients()
  }, [])

  useEffect(() => {
    if (shouldRefresh) {
      fetchPatients()
      setShouldRefresh(false)
    }
  }, [shouldRefresh])

  const handleAddSubmit = async (patient: Patient): Promise<void> => {
    try {
      await axios.post(`${process.env.NEXT_PUBLIC_API_URL}/api/patient/insert`, patient, {
        headers: { 'Content-Type': 'application/json' },
        withCredentials: true,
      })
      setIsAddOpen(false)
      setAddForm(emptyPatient)
      setShouldRefresh(true)
    } catch (err) {
      console.error('Erreur ajout patient', err)
    }
  }

  const handleViewPatient = (id: string): void => {
    router.push(`/patient/${id}`)
  }

  const handleAddChange = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>): void => {
    setAddForm(prev => ({ ...prev, [e.target.name]: e.target.value }))
  }

  const handleEditChange = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>): void => {
    setEditForm(prev => ({ ...prev, [e.target.name]: e.target.value }))
  }

  const handleDeletePatient = async (id: string): Promise<void> => {
    try {
      await axios.delete(`${process.env.NEXT_PUBLIC_API_URL}/api/patient/delete/${id}`, {
        withCredentials: true,
      })
      setShouldRefresh(true)
    } catch (err) {
      console.error('Erreur suppression patient', err)
    }
  }

  const handleUpdatePatient = (patient: Patient): void => {
    setEditForm(patient)
    setIsEditOpen(true)
  }

  const handleEditSubmit = async (patient: Patient): Promise<void> => {
    if (!patient.id) return
    try {
      await axios.put(`${process.env.NEXT_PUBLIC_API_URL}/api/patient/update/${patient.id}`, patient, {
        headers: { 'Content-Type': 'application/json' },
        withCredentials: true,
      })
      setIsEditOpen(false)
      setEditForm({ id: undefined, ...emptyPatient })
      setShouldRefresh(true)
    } catch (err) {
      console.error('Erreur mise à jour patient', err)
    }
  }

  const handleAddClose = (): void => {
    setIsAddOpen(false)
    setAddForm(emptyPatient)
  }

  const handleEditClose = (): void => {
    setIsEditOpen(false)
    setEditForm({ id: undefined, ...emptyPatient })
  }

  return (
      <AuthGuard>
        <PatientTable
            patients={patients}
            loading={loading}
            onView={handleViewPatient}
            onDelete={handleDeletePatient}
            onUpdate={handleUpdatePatient}
            onAddClick={() => setIsAddOpen(true)}
        />

        <AddPatientModal
            isOpen={isAddOpen}
            onClose={handleAddClose}
            onSubmit={handleAddSubmit}
            formData={addForm}
            onChange={handleAddChange}
        />

        <EditPatientModal
            isOpen={isEditOpen}
            onClose={handleEditClose}
            onSubmit={handleEditSubmit}
            formData={editForm}
            onChange={handleEditChange}
        />
      </AuthGuard>
  )
}
