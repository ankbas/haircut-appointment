export type Service = { id: number; audience: 'MEN' | 'WOMEN'; type: string; displayName: string; price: number; durationMinutes: number }
export type Professional = { id: number; name: string; bio: string; active: boolean; serviceIds: number[] }
export type AvailabilitySlot = { startTime: string; endTime: string }
export type Appointment = { id: number; confirmationNumber: string; customerName: string; customerPhone: string; customerEmail: string; professionalId: number; professionalName: string; serviceId: number; serviceType: string; price: number; durationMinutes: number; startTime: string; endTime: string; status: 'BOOKED' | 'COMPLETED' | 'CANCELLED' | 'NO_SHOW' }
type BookingRequest = { customerName: string; customerPhone: string; customerEmail: string; professionalId: number; serviceId: number; startTime: string }

const request = async <T>(path: string, options?: RequestInit): Promise<T> => {
  const response = await fetch(path, { ...options, headers: { 'Content-Type': 'application/json', ...options?.headers } })
  if (!response.ok) {
    const problem = await response.json().catch(() => null)
    throw new Error(problem?.detail ?? problem?.title ?? 'Something went wrong. Please try again.')
  }
  if (response.status === 204) return undefined as T
  return response.json() as Promise<T>
}

const authRequest = <T>(path: string, options?: RequestInit) => request<T>(path, {
  ...options, headers: { Authorization: `Bearer ${sessionStorage.getItem('atelier_token') ?? ''}`, ...options?.headers },
})

export const api = {
  services: () => request<Service[]>('/api/services'),
  professionals: (serviceId: number) => request<Professional[]>(`/api/professionals?serviceId=${serviceId}`),
  availability: (professionalId: number, serviceId: number, date: string) => request<AvailabilitySlot[]>(`/availability?professionalId=${professionalId}&serviceId=${serviceId}&date=${date}`),
  book: (body: BookingRequest) => request<Appointment>('/api/appointments', { method: 'POST', body: JSON.stringify(body) }),
  lookup: (confirmationNumber: string, email: string) => request<Appointment>(`/api/customer/appointments/lookup?confirmationNumber=${encodeURIComponent(confirmationNumber)}&email=${encodeURIComponent(email)}`),
  cancel: (confirmationNumber: string, email: string) => request<Appointment>('/api/customer/appointments/cancel', { method: 'PATCH', body: JSON.stringify({ confirmationNumber, email }) }),
  reschedule: (confirmationNumber: string, email: string, startTime: string) => request<Appointment>('/api/customer/appointments/reschedule', { method: 'PATCH', body: JSON.stringify({ confirmationNumber, email, startTime }) }),
  login: (username: string, password: string) => request<{ token: string; username: string; role: string }>('/api/auth/login', { method: 'POST', body: JSON.stringify({ username, password }) }),
  adminAppointments: (from: string, to: string) => authRequest<Appointment[]>(`/api/admin/appointments?from=${from}&to=${to}`),
  updateStatus: (id: number, status: Appointment['status']) => authRequest<Appointment>(`/api/admin/appointments/${id}/status`, { method: 'PATCH', body: JSON.stringify({ status }) }),
  adminProfessionals: () => authRequest<Professional[]>('/api/admin/professionals'),
  createService: (body: Omit<Service, 'id' | 'displayName'>) => authRequest<Service>('/api/admin/services', { method: 'POST', body: JSON.stringify(body) }),
  createProfessional: (body: { name: string; bio: string; active: boolean; serviceIds: number[] }) => authRequest<Professional>('/api/admin/professionals', { method: 'POST', body: JSON.stringify(body) }),
  setWorkingHours: (professionalId: number, day: string, startTime: string, endTime: string) => authRequest<void>(`/api/admin/professionals/${professionalId}/working-hours/${day}`, { method: 'PUT', body: JSON.stringify({ startTime, endTime }) }),
  addTimeOff: (professionalId: number, startsAt: string, endsAt: string, reason: string) => authRequest(`/api/admin/professionals/${professionalId}/time-off`, { method: 'POST', body: JSON.stringify({ startsAt, endsAt, reason }) }),
}
